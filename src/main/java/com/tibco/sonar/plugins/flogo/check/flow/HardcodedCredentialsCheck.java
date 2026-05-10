package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.Map;
import java.util.regex.Pattern;

@Rule(key = "HardcodedCredentials", name = "Credentials should not be hardcoded", priority = Priority.CRITICAL, tags = {
        "security", "cwe", "owasp-a07" })
public class HardcodedCredentialsCheck extends AbstractFlowCheck {

    private static final Pattern CREDENTIAL_KEYS = Pattern.compile(
            "password|secret|apikey|api_key|token|auth|credential|private.?key",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            FlogoActivity activity = task.getActivity();
            checkMap(activity.getInput(), task, flow, app, "input");
            checkMap(activity.getSettings(), task, flow, app, "settings");
        }
    }

    private void checkMap(Map<String, Object> map, FlogoTask task, FlogoFlow flow, FlogoApp app, String context) {
        if (map == null)
            return;
        for (var entry : map.entrySet()) {
            if (CREDENTIAL_KEYS.matcher(entry.getKey()).find()) {
                if (entry.getValue() instanceof String val) {
                    if (!val.isEmpty() && !val.startsWith("=$property") && !val.startsWith("=$.")
                            && !val.startsWith("SECRET:")) {
                        addIssue(
                                "Task '" + task.getId() + "' in flow '" + flow.getName()
                                        + "' has hardcoded credential in " + context + " field '" + entry.getKey()
                                        + "'. Use $property[...] with secrets management.",
                                taskLine(app, task.getId()));
                    }
                }
            }
        }
    }
}
