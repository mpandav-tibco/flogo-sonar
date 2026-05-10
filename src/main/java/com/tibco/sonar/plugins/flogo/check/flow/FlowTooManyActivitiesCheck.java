package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;

@Rule(key = "FlowTooManyActivities", name = "Flow has too many activities", priority = Priority.MAJOR, tags = {
        "complexity", "maintainability" })
public class FlowTooManyActivitiesCheck extends AbstractFlowCheck {

    @RuleProperty(key = "maxActivities", description = "Maximum activities per flow", defaultValue = "20")
    private int maxActivities = 20;

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        int count = flow.getTasks().size();
        if (count > maxActivities) {
            addIssue(
                    "Flow '" + flow.getName() + "' has " + count + " activities (max " + maxActivities
                            + "). Consider splitting into subflows for better maintainability.",
                    flowLine(app, flow));
        }
    }
}
