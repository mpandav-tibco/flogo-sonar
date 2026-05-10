package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "FlowNoDescription", name = "Flow should have a description", priority = Priority.MINOR, tags = {
        "convention", "documentation" })
public class FlowNoDescriptionCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        if (flow.getDescription() == null || flow.getDescription().trim().isEmpty()) {
            addIssue(
                    "Flow '" + flow.getName()
                            + "' has no description. Add a meaningful description to improve maintainability.",
                    flowLine(app, flow));
        }
    }
}
