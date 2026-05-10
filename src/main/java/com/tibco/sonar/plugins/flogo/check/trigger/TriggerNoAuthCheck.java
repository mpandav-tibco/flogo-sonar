package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "TriggerNoAuth", name = "REST triggers should have authentication", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a07" })
public class TriggerNoAuthCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isRestTrigger())
            return;
        String authType = trigger.getSettingAsString("authenticationType");
        if (authType == null || "None".equalsIgnoreCase(authType) || authType.isEmpty()) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue(
                    "REST trigger '" + triggerName + "' has no authentication (authenticationType='" + authType
                            + "'). Configure authentication to secure your endpoints.",
                    triggerLine(app, trigger));
        }
    }
}
