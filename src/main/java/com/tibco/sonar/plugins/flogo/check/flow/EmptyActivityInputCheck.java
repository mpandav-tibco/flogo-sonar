package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.Map;

@Rule(key = "EmptyActivityInput", name = "Activities should have input mappings configured", priority = Priority.MINOR, tags = {
        "suspicious", "maintainability" })
public class EmptyActivityInputCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.isStartTask() || task.isReturnTask())
                continue;
            if (task.getActivity() == null)
                continue;
            if (task.getActivity().isNoopRef() || task.getActivity().isLogRef())
                continue;
            Map<String, Object> input = task.getActivity().getInput();
            if (input == null || input.isEmpty()) {
                addIssue(
                        "Task '" + task.getId() + "' in flow '" + flow.getName()
                                + "' has no input mappings. Verify this is intentional.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
