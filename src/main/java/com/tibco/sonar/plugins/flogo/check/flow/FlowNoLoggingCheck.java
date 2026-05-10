package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "FlowNoLogging", name = "Flow has no logging activities", priority = Priority.MINOR, tags = {
        "maintainability", "observability" })
public class FlowNoLoggingCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Skip tiny flows (Start + Return only)
        if (flow.getTasks().size() <= 2)
            return;

        boolean hasLog = flow.getTasks().stream()
                .anyMatch(t -> t.getActivity() != null && t.getActivity().isLogRef());
        if (!hasLog) {
            addIssue("Flow '" + flow.getName()
                    + "' has no logging activities. Add log activities for observability and troubleshooting.",
                    flowLine(app, flow));
        }
    }
}
