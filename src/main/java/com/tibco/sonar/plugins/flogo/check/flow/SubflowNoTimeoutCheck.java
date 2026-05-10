package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "SubflowNoTimeout", name = "Subflow activity should have an execution timeout", priority = Priority.MAJOR, tags = {
        "reliability", "resilience" })
public class SubflowNoTimeoutCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null || !task.getActivity().isSubflowRef())
                continue;

            Object timeout = task.getActivity().getSettingValue("execTimeout");
            boolean hasTimeout = timeout != null && !("0".equals(timeout.toString()));
            if (!hasTimeout) {
                addIssue("Subflow activity '" + task.getId() + "' in flow '" + flow.getName()
                        + "' has no execution timeout (or timeout=0). Set execTimeout to prevent indefinite waits if the subflow hangs.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
