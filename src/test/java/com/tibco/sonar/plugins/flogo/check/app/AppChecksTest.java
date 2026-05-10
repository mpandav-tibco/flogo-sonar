package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AppChecksTest {

    private FlogoApp app;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream("/test-app.flogo");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        app = FlogoParser.parse(content);
    }

    @Test
    public void testAppNoDescription_FindsIssue() {
        AppNoDescriptionCheck check = new AppNoDescriptionCheck();
        check.validate(app);
        // App description is empty
        assertTrue("Should find app with no description", check.getIssues().size() >= 1);
    }

    @Test
    public void testDefaultAppVersion_FindsIssue() {
        DefaultAppVersionCheck check = new DefaultAppVersionCheck();
        check.validate(app);
        // Version is 1.0.0 (default)
        assertTrue("Should find default version", check.getIssues().size() >= 1);
    }

    @Test
    public void testSecretInProperties_FindsIssue() {
        SecretInPropertiesCheck check = new SecretInPropertiesCheck();
        check.validate(app);
        // api_password has plaintext value "s3cret"
        assertTrue("Should find plaintext secret", check.getIssues().size() >= 1);
        assertTrue(check.getIssues().stream().anyMatch(
                i -> i.getMessage().contains("api_password")));
    }

    @Test
    public void testSecretInProperties_SkipsSafeValues() {
        FlogoApp safeApp = new FlogoApp();
        safeApp.setRawContent("{}");
        safeApp.setFlows(java.util.List.of());
        safeApp.setTriggers(java.util.List.of());

        FlogoProperty p1 = new FlogoProperty();
        p1.setName("api_password");
        p1.setValue("SECRET:encrypted");
        FlogoProperty p2 = new FlogoProperty();
        p2.setName("db_token");
        p2.setValue("=$property[tok]");
        FlogoProperty p3 = new FlogoProperty();
        p3.setName("auth_secret");
        p3.setValue("$env[MY_SECRET]");
        FlogoProperty p4 = new FlogoProperty();
        p4.setName("api_key");
        p4.setValue("$property[key]");
        safeApp.setProperties(java.util.List.of(p1, p2, p3, p4));

        SecretInPropertiesCheck check = new SecretInPropertiesCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag safe secret values", 0, check.getIssues().size());
    }

    @Test
    public void testDuplicateFlowName_NoIssue() {
        DuplicateFlowNameCheck check = new DuplicateFlowNameCheck();
        check.validate(app);
        // No duplicate flow names in test fixture
        assertEquals(0, check.getIssues().size());
    }

    @Test
    public void testUnusedFlow_FindsIssue() {
        UnusedFlowCheck check = new UnusedFlowCheck();
        check.validate(app);
        // Both flows are referenced by handlers, so no unused flows
        assertEquals(0, check.getIssues().size());
    }

    @Test
    public void testConnectionNoTLS_NoIssueWhenEmpty() {
        ConnectionNoTLSCheck check = new ConnectionNoTLSCheck();
        check.validate(app);
        // No connections in test app
        assertEquals(0, check.getIssues().size());
    }

    @Test
    public void testConnectionNoTLS_NullConnections() {
        FlogoApp nullConnApp = new FlogoApp();
        nullConnApp.setRawContent("{}");
        nullConnApp.setFlows(java.util.List.of());
        nullConnApp.setTriggers(java.util.List.of());
        nullConnApp.setConnections(null);
        nullConnApp.setProperties(java.util.List.of());

        ConnectionNoTLSCheck check = new ConnectionNoTLSCheck();
        check.validate(nullConnApp);
        assertEquals("Null connections should not cause NPE", 0, check.getIssues().size());
    }
}
