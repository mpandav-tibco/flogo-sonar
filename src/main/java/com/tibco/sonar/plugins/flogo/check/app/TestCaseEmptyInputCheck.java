package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "TestCaseEmptyInput", name = "Test case should have meaningful input data", priority = Priority.MINOR, tags = {
        "tests", "maintainability" })
public class TestCaseEmptyInputCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (Map.Entry<String, Map<String, Object>> entry : app.getTestCases().entrySet()) {
            Map<String, Object> tc = entry.getValue();
            boolean hasInputs = (boolean) tc.getOrDefault("hasInputs", false);
            if (!hasInputs) {
                String testName = (String) tc.getOrDefault("name", entry.getKey());
                String flowName = (String) tc.getOrDefault("flowName", "");
                addIssue("Test case '" + testName + "' for flow '" + flowName
                        + "' has empty input data. Provide representative test inputs to exercise the flow logic.",
                        FlogoParser.findElementLine(app.getRawContent(), flowName));
            }
        }
    }
}
