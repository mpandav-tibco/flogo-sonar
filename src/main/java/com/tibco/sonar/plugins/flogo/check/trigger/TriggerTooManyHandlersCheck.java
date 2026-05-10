package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;

@Rule(key = "TriggerTooManyHandlers", name = "Trigger should not have too many handlers", priority = Priority.MAJOR, tags = {
        "complexity", "maintainability" })
public class TriggerTooManyHandlersCheck extends AbstractTriggerCheck {

    @RuleProperty(key = "maxHandlers", description = "Maximum handlers per trigger", defaultValue = "10")
    private int maxHandlers = 10;

    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        int count = trigger.getHandlers().size();
        if (count > maxHandlers) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue(
                    "Trigger '" + triggerName + "' has " + count + " handlers (max " + maxHandlers
                            + "). Consider splitting into multiple triggers for better organization.",
                    triggerLine(app, trigger));
        }
    }
}
