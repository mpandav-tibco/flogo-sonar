package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "EmptyTransitionCondition", name = "Conditional transition has empty expression", priority = Priority.CRITICAL, tags = {
        "bug", "reliability" })
public class EmptyTransitionConditionCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoLink link : flow.getLinks()) {
            if ("expression".equalsIgnoreCase(link.getType())) {
                String val = link.getValue();
                if (val == null || val.trim().isEmpty()) {
                    String label = link.hasLabel() ? " '" + link.getLabel() + "'" : "";
                    addIssue("Transition" + label + " from '" + link.getFrom() + "' to '"
                            + link.getTo() + "' in flow '" + flow.getName()
                            + "' has type=expression but an empty condition value. "
                            + "This will always evaluate to false, making the path unreachable.",
                            taskLine(app, link.getFrom()));
                }
            }
        }
    }
}
