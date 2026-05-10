package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "TransitionLabel", name = "Transitions should have labels", priority = Priority.MINOR, tags = {
        "convention", "readability" })
public class TransitionLabelCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoLink link : flow.getLinks()) {
            if (!link.hasLabel()) {
                addIssue(
                        "Link from '" + link.getFrom() + "' to '" + link.getTo() + "' in flow '" + flow.getName()
                                + "' has no label. Add labels to improve flow readability.",
                        flowLine(app, flow));
            }
        }
    }
}
