package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FlowChecksTest {

    private FlogoApp app;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream("/test-app.flogo");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        app = FlogoParser.parse(content);
    }

    @Test
    public void testFlowNoDescription_FindsIssue() {
        FlowNoDescriptionCheck check = new FlowNoDescriptionCheck();
        check.validate(app);
        // CreateOrder flow has no description
        assertTrue("Should find flow with no description", check.getIssues().size() >= 1);
        assertTrue(check.getIssues().stream().anyMatch(
                i -> i.getMessage().contains("CreateOrder")));
    }

    @Test
    public void testFlowMissingReturn_FindsIssue() {
        FlowMissingReturnCheck check = new FlowMissingReturnCheck();
        check.validate(app);
        // CreateOrder has no return activity
        assertTrue("Should find flow missing return", check.getIssues().size() >= 1);
        assertTrue(check.getIssues().stream().anyMatch(
                i -> i.getMessage().contains("CreateOrder")));
    }

    @Test
    public void testFlowDeadEnd_FindsIssue() {
        FlowDeadEndCheck check = new FlowDeadEndCheck();
        check.validate(app);
        // REST_2 in CreateOrder has no outgoing link and is not a return
        assertTrue("Should find dead end task", check.getIssues().size() >= 1);
    }

    @Test
    public void testHardcodedURL_FindsIssue() {
        // Build an app with a REST activity that has a hardcoded URI using the correct
        // key
        FlogoApp urlApp = new FlogoApp();
        urlApp.setRawContent("{}");
        FlogoFlow flow = new FlogoFlow();
        flow.setId("flow:test");
        flow.setName("test_flow");
        FlogoTask task = new FlogoTask();
        task.setId("REST_1");
        FlogoActivity activity = new FlogoActivity();
        activity.setRef("#rest");
        java.util.Map<String, Object> input = new java.util.LinkedHashMap<>();
        input.put("Uri", "http://example.com/api");
        activity.setInput(input);
        task.setActivity(activity);
        flow.getTasks().add(task);
        urlApp.setFlows(java.util.List.of(flow));
        urlApp.setTriggers(java.util.List.of());
        urlApp.setProperties(java.util.List.of());

        HardcodedURLCheck check = new HardcodedURLCheck();
        check.validate(urlApp);
        assertTrue("Should find hardcoded URL", check.getIssues().size() >= 1);
    }

    @Test
    public void testHardcodedCredentials_FindsIssue() {
        HardcodedCredentialsCheck check = new HardcodedCredentialsCheck();
        check.validate(app);
        // CreateOrder has hardcoded password "admin123"
        assertTrue("Should find hardcoded credentials", check.getIssues().size() >= 1);
        assertTrue(check.getIssues().stream().anyMatch(
                i -> i.getMessage().contains("password")));
    }

    @Test
    public void testHardcodedCredentials_SkipsPropertyRef() {
        // Build a mini app where the password uses $property[...]
        FlogoApp safeApp = buildAppWithCredential("$property[db.password]");
        HardcodedCredentialsCheck check = new HardcodedCredentialsCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag $property references", 0, check.getIssues().size());
    }

    @Test
    public void testHardcodedCredentials_SkipsEnvRef() {
        FlogoApp safeApp = buildAppWithCredential("$env[DB_PASS]");
        HardcodedCredentialsCheck check = new HardcodedCredentialsCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag $env references", 0, check.getIssues().size());
    }

    @Test
    public void testHardcodedCredentials_SkipsSecret() {
        FlogoApp safeApp = buildAppWithCredential("SECRET:encrypted-value");
        HardcodedCredentialsCheck check = new HardcodedCredentialsCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag SECRET: prefix", 0, check.getIssues().size());
    }

    @Test
    public void testHardcodedCredentials_SkipsMappingExpr() {
        FlogoApp safeApp = buildAppWithCredential("=$property[pw]");
        HardcodedCredentialsCheck check = new HardcodedCredentialsCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag = expressions", 0, check.getIssues().size());
    }

    @Test
    public void testCircularLink_NoIssueOnLinearFlow() {
        CircularLinkCheck check = new CircularLinkCheck();
        check.validate(app);
        assertEquals("Linear flow should have no circular links", 0, check.getIssues().size());
    }

    @Test
    public void testBrokenResolverReference_NoIssueOnValidRefs() {
        BrokenResolverReferenceCheck check = new BrokenResolverReferenceCheck();
        check.validate(app);
        // get_orders references $activity[REST_1] which exists
        assertEquals("Valid refs should have no issues", 0, check.getIssues().size());
    }

    @Test
    public void testFlowNamingConvention_FindsIssue() {
        FlowNamingConventionCheck check = new FlowNamingConventionCheck();
        check.validate(app);
        // CreateOrder is PascalCase, not snake_case
        assertTrue("Should find naming convention issue", check.getIssues().size() >= 1);
    }

    @Test
    public void testClearIssues() {
        FlowNoDescriptionCheck check = new FlowNoDescriptionCheck();
        check.validate(app);
        assertTrue(check.getIssues().size() > 0);
        check.clearIssues();
        assertEquals(0, check.getIssues().size());
    }

    @Test
    public void testGetRuleKey() {
        FlowNoDescriptionCheck check = new FlowNoDescriptionCheck();
        assertEquals("FlowNoDescription", check.getRuleKey());
    }

    private FlogoApp buildAppWithCredential(String passwordValue) {
        FlogoApp testApp = new FlogoApp();
        testApp.setRawContent("{}");
        FlogoFlow flow = new FlogoFlow();
        flow.setId("flow:test");
        flow.setName("test_flow");
        FlogoTask task = new FlogoTask();
        task.setId("REST_1");
        FlogoActivity activity = new FlogoActivity();
        activity.setRef("#rest");
        java.util.Map<String, Object> input = new java.util.LinkedHashMap<>();
        input.put("password", passwordValue);
        activity.setInput(input);
        task.setActivity(activity);
        flow.getTasks().add(task);
        testApp.setFlows(java.util.List.of(flow));
        testApp.setTriggers(java.util.List.of());
        testApp.setProperties(java.util.List.of());
        return testApp;
    }
}
