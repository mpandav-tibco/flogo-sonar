package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "ActivitySkipTLSVerify", name = "Activity should not skip TLS certificate verification", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a02" })
public class ActivitySkipTLSVerifyCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;

            // Check activity settings for skipTlsVerify / skipSSLVerify
            Object skipTls = task.getActivity().getSettingValue("skipTlsVerify");
            Object skipSSL = task.getActivity().getSettingValue("skipSSLVerify");

            if ("true".equalsIgnoreCase(String.valueOf(skipTls)) || Boolean.TRUE.equals(skipTls)) {
                addIssue("Activity '" + task.getId() + "' in flow '" + flow.getName()
                        + "' has skipTlsVerify=true. This disables certificate validation "
                        + "and exposes the connection to man-in-the-middle attacks.",
                        taskLine(app, task.getId()));
            }
            if ("true".equalsIgnoreCase(String.valueOf(skipSSL)) || "True".equals(String.valueOf(skipSSL))
                    || Boolean.TRUE.equals(skipSSL)) {
                addIssue("Activity '" + task.getId() + "' in flow '" + flow.getName()
                        + "' has skipSSLVerify=true. This disables certificate validation "
                        + "and exposes the connection to man-in-the-middle attacks.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
