package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "RestErrorSilenced", name = "REST activity silently ignoring HTTP errors", priority = Priority.MAJOR, tags = {
        "reliability", "error-handling" })
public class RestErrorSilencedCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null || !task.getActivity().isRestRef())
                continue;

            Object throwError = task.getActivity().getOutputValue("throwError");
            Object configResp = task.getActivity().getOutputValue("configureResponseCodes");

            boolean throwsOnError = "true".equals(String.valueOf(throwError));
            boolean configuresResp = "true".equals(String.valueOf(configResp));

            // Check if there are conditional links checking statusCode from this task
            boolean checksStatusCode = flow.getLinks().stream()
                    .filter(l -> task.getId().equals(l.getFrom()))
                    .anyMatch(l -> {
                        String val = l.getValue();
                        return val != null && val.contains("statusCode");
                    });

            if (!throwsOnError && !configuresResp && !checksStatusCode) {
                addIssue("REST activity '" + task.getId() + "' in flow '" + flow.getName()
                        + "' does not handle HTTP errors. Enable throwError, configure response codes, "
                        + "or add conditional transitions checking statusCode.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
