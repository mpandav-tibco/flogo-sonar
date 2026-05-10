package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "EmptyFlow", name = "Flow has no meaningful activities", priority = Priority.MAJOR, tags = {
        "maintainability", "code-smell" })
public class EmptyFlowCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        long meaningfulTasks = flow.getTasks().stream()
                .filter(t -> !t.isStartTask())
                .count();
        if (meaningfulTasks == 0) {
            addIssue("Flow '" + flow.getName()
                    + "' has no meaningful activities (only Start or empty). Remove the flow or add activities.",
                    flowLine(app, flow));
        }
    }
}
