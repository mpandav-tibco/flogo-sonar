package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.List;

@Rule(key = "FlowDeadEnd", name = "Task should not be a dead end", priority = Priority.MAJOR, tags = { "bug",
        "reliability" })
public class FlowDeadEndCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.isReturnTask() || task.isStartTask())
                continue;
            List<FlogoLink> outgoing = flow.getOutgoingLinks(task.getId());
            if (outgoing.isEmpty()) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                        + "' has no outgoing link (dead end). Connect it to subsequent tasks or a Return activity.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
