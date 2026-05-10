package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;

@Rule(key = "FlowComplexity", name = "Flow has too many conditional branches", priority = Priority.MAJOR, tags = {
        "maintainability", "complexity" })
public class FlowComplexityCheck extends AbstractFlowCheck {

    @RuleProperty(key = "maxBranches", description = "Maximum number of conditional branches per flow", defaultValue = "10")
    public int maxBranches = 10;

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Count conditional links (branching points) as a measure of flow complexity
        long conditionalLinks = flow.getLinks().stream()
                .filter(FlogoLink::isConditional)
                .count();
        if (conditionalLinks > maxBranches) {
            addIssue("Flow '" + flow.getName() + "' has " + conditionalLinks
                    + " conditional branches (max " + maxBranches
                    + "). Consider splitting into subflows to reduce complexity.",
                    flowLine(app, flow));
        }
    }
}
