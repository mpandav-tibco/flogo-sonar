package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TriggerChecksTest {

    private FlogoApp app;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream("/test-app.flogo");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        app = FlogoParser.parse(content);
    }

    @Test
    public void testTriggerDefaultPort_FindsIssue() {
        TriggerDefaultPortCheck check = new TriggerDefaultPortCheck();
        check.validate(app);
        // Port is 9999 (default)
        assertTrue("Should find default port", check.getIssues().size() >= 1);
    }

    @Test
    public void testTriggerHardcodedCredentials_NoIssue() {
        TriggerHardcodedCredentialsCheck check = new TriggerHardcodedCredentialsCheck();
        check.validate(app);
        // No credential settings in test trigger
        assertEquals(0, check.getIssues().size());
    }

    @Test
    public void testTriggerHardcodedCredentials_SkipsSafeValues() {
        FlogoApp safeApp = new FlogoApp();
        safeApp.setRawContent("{}");
        safeApp.setFlows(java.util.List.of());
        safeApp.setProperties(java.util.List.of());

        FlogoTrigger trigger = new FlogoTrigger();
        trigger.setId("t1");
        trigger.setName("RestTrigger");
        trigger.setRef("#rest");
        java.util.Map<String, Object> settings = new java.util.LinkedHashMap<>();
        settings.put("password", "$property[my.pass]");
        settings.put("apiKey", "$env[API_KEY]");
        settings.put("secret", "SECRET:enc");
        settings.put("token", "=$flow.input.token");
        trigger.setSettings(settings);
        trigger.setHandlers(java.util.List.of());
        safeApp.setTriggers(java.util.List.of(trigger));

        TriggerHardcodedCredentialsCheck check = new TriggerHardcodedCredentialsCheck();
        check.validate(safeApp);
        assertEquals("Should NOT flag safe credential values", 0, check.getIssues().size());
    }
}
