package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "RestActivityTimeout", name = "REST activities should have a timeout configured", priority = Priority.MAJOR, tags = {
        "performance", "reliability" })
public class RestActivityTimeoutCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null || !task.getActivity().isRestRef())
                continue;
            FlogoActivity activity = task.getActivity();
            Object timeout = activity.getInputValue("Timeout");
            if (timeout == null) {
                timeout = activity.getSettingValue("Timeout");
            }
            if (timeout == null || "0".equals(timeout.toString()) || "0.0".equals(timeout.toString())) {
                addIssue("REST activity '" + task.getId() + "' in flow '" + flow.getName()
                        + "' has no timeout or timeout=0. Set a reasonable timeout to prevent hanging requests.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
