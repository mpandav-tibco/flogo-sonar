package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "TriggerNoDescription", name = "Trigger handlers should have descriptions", priority = Priority.MINOR, tags = {
        "convention", "documentation" })
public class TriggerNoDescriptionCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        for (FlogoHandler handler : trigger.getHandlers()) {
            if (handler.getDescription() == null || handler.getDescription().trim().isEmpty()) {
                String handlerName = handler.getName() != null ? handler.getName() : handler.getPath();
                addIssue(
                        "Trigger handler '" + handlerName + "' in trigger '" + trigger.getId()
                                + "' has no description.",
                        FlogoParser.findElementLine(app.getRawContent(),
                                handlerName != null ? handlerName : trigger.getId()));
            }
        }
    }
}
