package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "CORSWildcard", name = "CORS should not allow all origins", priority = Priority.MAJOR, tags = {
        "security", "owasp-a05" })
public class CORSWildcardCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        Object enableCORS = trigger.getSetting("enableCORS");
        if (enableCORS == null || !"true".equalsIgnoreCase(String.valueOf(enableCORS))) {
            return;
        }

        Object corsOrigins = trigger.getSetting("corsOrigins");
        if (corsOrigins != null && "*".equals(String.valueOf(corsOrigins).trim())) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue("Trigger '" + triggerName
                    + "' uses CORS with wildcard origin (*). This allows any website to make requests to your API. "
                    + "Restrict corsOrigins to specific trusted domains.",
                    triggerLine(app, trigger));
        }
    }
}
