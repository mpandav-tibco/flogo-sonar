package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "EmptyActivityDescription", name = "Activities should have descriptions", priority = Priority.MINOR, tags = {
        "convention", "documentation" })
public class EmptyActivityDescriptionCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.isStartTask())
                continue; // Skip Start noop
            if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName() + "' has no description.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
