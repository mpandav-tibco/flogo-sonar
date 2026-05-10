package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "DuplicateFlowName", name = "Flow names should be unique", priority = Priority.CRITICAL, tags = { "bug" })
public class DuplicateFlowNameCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        Map<String, Integer> nameCount = new HashMap<>();
        for (FlogoFlow flow : app.getFlows()) {
            if (flow.getName() != null) {
                nameCount.merge(flow.getName(), 1, Integer::sum);
            }
        }
        for (var entry : nameCount.entrySet()) {
            if (entry.getValue() > 1) {
                addIssue(
                        "Flow name '" + entry.getKey() + "' appears " + entry.getValue()
                                + " times. Flow names must be unique.",
                        FlogoParser.findElementLine(app.getRawContent(), entry.getKey()));
            }
        }
    }
}
