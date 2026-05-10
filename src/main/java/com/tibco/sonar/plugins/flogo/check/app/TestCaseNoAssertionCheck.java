package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "TestCaseNoAssertion", name = "Test case should have assertions", priority = Priority.MAJOR, tags = {
        "tests", "maintainability" })
public class TestCaseNoAssertionCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (Map.Entry<String, Map<String, Object>> entry : app.getTestCases().entrySet()) {
            Map<String, Object> tc = entry.getValue();
            int assertions = (int) tc.getOrDefault("assertionCount", 0);
            if (assertions == 0) {
                String testName = (String) tc.getOrDefault("name", entry.getKey());
                String flowName = (String) tc.getOrDefault("flowName", "");
                addIssue("Test case '" + testName + "' for flow '" + flowName
                        + "' has no assertions. Add assertions to validate flow outputs and ensure correctness.",
                        FlogoParser.findElementLine(app.getRawContent(), flowName));
            }
        }
    }
}
