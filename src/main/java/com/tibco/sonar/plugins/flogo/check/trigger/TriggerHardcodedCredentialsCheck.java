package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.regex.Pattern;

@Rule(key = "TriggerHardcodedCredentials", name = "Trigger settings contain hardcoded credentials", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a07" })
public class TriggerHardcodedCredentialsCheck extends AbstractTriggerCheck {

    private static final Pattern CREDENTIAL_KEY = Pattern.compile(
            "(?i)(password|passwd|secret|apikey|api_key|token|credential|auth)",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (trigger.getSettings() == null)
            return;

        for (java.util.Map.Entry<String, Object> entry : trigger.getSettings().entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val == null)
                continue;
            String strVal = String.valueOf(val).trim();
            if (strVal.isEmpty())
                continue;

            // Skip property references and SECRET: encrypted values
            if (strVal.startsWith("$property[") || strVal.startsWith("$env[")
                    || strVal.startsWith("SECRET:") || strVal.startsWith("=")) {
                continue;
            }

            if (CREDENTIAL_KEY.matcher(key).find()) {
                String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
                addIssue("Trigger '" + triggerName + "' has hardcoded value for setting '"
                        + key + "'. Use application properties ($property[\"..\"]) "
                        + "or encrypted values (SECRET:...) instead of plain text credentials.",
                        triggerLine(app, trigger));
            }
        }

        // Also check handler-level settings
        for (FlogoHandler handler : trigger.getHandlers()) {
            if (handler.getSettings() == null)
                continue;
            for (java.util.Map.Entry<String, Object> entry : handler.getSettings().entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val == null)
                    continue;
                String strVal = String.valueOf(val).trim();
                if (strVal.isEmpty() || strVal.startsWith("$property[")
                        || strVal.startsWith("$env[") || strVal.startsWith("SECRET:")
                        || strVal.startsWith("=")) {
                    continue;
                }
                if (CREDENTIAL_KEY.matcher(key).find()) {
                    String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
                    addIssue("Handler on trigger '" + triggerName + "' has hardcoded value for setting '"
                            + key + "'. Use application properties or encrypted values instead.",
                            triggerLine(app, trigger));
                }
            }
        }
    }
}
