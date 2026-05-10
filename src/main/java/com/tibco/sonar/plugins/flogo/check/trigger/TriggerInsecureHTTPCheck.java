package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "TriggerInsecureHTTP", name = "REST triggers should use HTTPS", priority = Priority.MAJOR, tags = {
        "security", "owasp-a02" })
public class TriggerInsecureHTTPCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isRestTrigger())
            return;
        Object secure = trigger.getSetting("secureConnection");
        if (secure == null || Boolean.FALSE.equals(secure) || "false".equalsIgnoreCase(String.valueOf(secure))) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue("REST trigger '" + triggerName
                    + "' does not use a secure (HTTPS) connection. Enable secureConnection for production deployments.",
                    triggerLine(app, trigger));
        }
    }
}
