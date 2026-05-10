package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "SleepActivity", name = "Sleep activity should not be used in production flows", priority = Priority.MAJOR, tags = {
        "bad-practice", "performance" })
public class SleepActivityCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null || !task.getActivity().isSleepRef())
                continue;
            addIssue("Flow '" + flow.getName() + "' uses Sleep activity '" + task.getId()
                    + "'. Sleep blocks the goroutine and degrades performance. "
                    + "Use Timer triggers, Wait/Notify pattern, or retry with backoff instead.",
                    taskLine(app, task.getId()));
        }
    }
}
