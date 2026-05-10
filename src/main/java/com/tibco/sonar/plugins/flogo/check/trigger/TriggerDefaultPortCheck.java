package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "TriggerDefaultPort", name = "REST trigger using default port", priority = Priority.MINOR, tags = {
        "configuration", "maintainability" })
public class TriggerDefaultPortCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isRestTrigger() && !trigger.isGrpcTrigger() && !trigger.isGraphQLTrigger())
            return;

        Object port = trigger.getSetting("port");
        if (port != null) {
            int portNum;
            try {
                portNum = (int) Double.parseDouble(port.toString());
            } catch (NumberFormatException e) {
                return; // Uses property reference, OK
            }
            if (portNum == 9999 || portNum == 7879) {
                String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
                addIssue("Trigger '" + triggerName
                        + "' uses default port " + portNum
                        + ". Configure an explicit port via application properties ($property[\"..\"]) for deployment flexibility.",
                        triggerLine(app, trigger));
            }
        }
    }
}
