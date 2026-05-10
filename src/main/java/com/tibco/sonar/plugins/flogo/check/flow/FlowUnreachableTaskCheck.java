package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.Set;

@Rule(key = "FlowUnreachableTask", name = "All tasks should be reachable from Start", priority = Priority.CRITICAL, tags = {
        "bug", "dead-code" })
public class FlowUnreachableTaskCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        Set<String> reachable = flow.getReachableTaskIds();
        for (FlogoTask task : flow.getTasks()) {
            if (!reachable.contains(task.getId())) {
                addIssue(
                        "Task '" + task.getId() + "' in flow '" + flow.getName()
                                + "' is not reachable from the Start activity. Remove or connect it.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
