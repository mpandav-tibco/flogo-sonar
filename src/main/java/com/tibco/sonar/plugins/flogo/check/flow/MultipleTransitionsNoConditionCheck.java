package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "MultipleTransitionsNoCondition", name = "Multiple outgoing transitions should have conditions", priority = Priority.MAJOR, tags = {
        "bug", "reliability" })
public class MultipleTransitionsNoConditionCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        Map<String, List<FlogoLink>> outgoing = new HashMap<>();
        for (FlogoLink link : flow.getLinks()) {
            outgoing.computeIfAbsent(link.getFrom(), k -> new ArrayList<>()).add(link);
        }
        for (var entry : outgoing.entrySet()) {
            if (entry.getValue().size() > 1) {
                boolean allUnconditional = entry.getValue().stream().noneMatch(FlogoLink::isConditional);
                if (allUnconditional) {
                    addIssue("Task '" + entry.getKey() + "' in flow '" + flow.getName() + "' has "
                            + entry.getValue().size()
                            + " outgoing transitions but none have conditions. This may cause non-deterministic behavior.",
                            taskLine(app, entry.getKey()));
                }
            }
        }
    }
}
