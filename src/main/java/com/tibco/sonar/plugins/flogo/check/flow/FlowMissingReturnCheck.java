package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "FlowMissingReturn", name = "Flow should end with a Return activity", priority = Priority.MAJOR, tags = {
        "bug", "reliability" })
public class FlowMissingReturnCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        boolean hasReturn = flow.getTasks().stream()
                .anyMatch(t -> t.isReturnTask());
        if (!hasReturn) {
            addIssue(
                    "Flow '" + flow.getName()
                            + "' does not have a Return (actreturn) activity. Flows should explicitly return results.",
                    flowLine(app, flow));
        }
    }
}
