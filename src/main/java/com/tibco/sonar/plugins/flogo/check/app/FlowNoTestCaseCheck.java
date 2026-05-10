package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "FlowNoTestCase", name = "Flow should have at least one test case", priority = Priority.MAJOR, tags = {
        "tests", "maintainability" })
public class FlowNoTestCaseCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        if (app.getTestCases().isEmpty()) {
            // No test file at all — skip (covered by coverage metric showing 0%)
            return;
        }

        // Collect flow IDs that have test cases
        Set<String> testedFlowIds = new HashSet<>();
        for (Map<String, Object> tc : app.getTestCases().values()) {
            String flowId = (String) tc.getOrDefault("flowId", "");
            if (!flowId.isEmpty()) {
                testedFlowIds.add(flowId);
            }
        }

        // Check each flow
        for (FlogoFlow flow : app.getFlows()) {
            if (!testedFlowIds.contains(flow.getId())) {
                addIssue("Flow '" + flow.getName() + "' has no test case in the .flogotest file. "
                        + "Add a test case with assertions to validate flow behavior.",
                        FlogoParser.findElementLine(app.getRawContent(), flow.getName()));
            }
        }
    }
}
