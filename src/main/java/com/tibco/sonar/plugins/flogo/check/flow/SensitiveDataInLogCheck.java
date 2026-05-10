package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Arrays;
import java.util.List;

@Rule(key = "SensitiveDataInLog", name = "Log activity may expose sensitive data", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a3" })
public class SensitiveDataInLogCheck extends AbstractFlowCheck {

    private static final List<String> SENSITIVE_PATTERNS = Arrays.asList(
            "password", "passwd", "secret", "apikey", "api_key", "apiKey",
            "token", "authorization", "auth_token", "accessToken", "access_token",
            "credential", "private_key", "privateKey", "client_secret", "clientSecret");

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null || !task.getActivity().isLogRef())
                continue;

            Object msgObj = task.getActivity().getInputValue("message");
            if (msgObj == null)
                continue;
            String message = msgObj.toString();

            String messageLower = message.toLowerCase();
            for (String pattern : SENSITIVE_PATTERNS) {
                if (messageLower.contains(pattern.toLowerCase())) {
                    addIssue("Log activity '" + task.getId() + "' in flow '" + flow.getName()
                            + "' may log sensitive data ('" + pattern
                            + "'). Avoid logging credentials, tokens, or secrets.",
                            taskLine(app, task.getId()));
                    break;
                }
            }
        }
    }
}
