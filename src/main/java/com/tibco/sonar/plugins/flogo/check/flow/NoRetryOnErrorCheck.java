package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "NoRetryOnError", name = "External call activities should have retry configuration", priority = Priority.MINOR, tags = {
        "reliability", "resilience" })
public class NoRetryOnErrorCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            // Only check external call activities (REST, JDBC, etc.)
            if (!task.getActivity().isRestRef())
                continue;
            if (!task.hasRetryConfig()) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                        + "' makes an external call but has no retry configuration. Consider adding retryOnError settings for resilience.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
