package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "UnboundTriggerHandler", name = "Trigger handler must have a flow binding", priority = Priority.CRITICAL, tags = {
        "bug", "reliability" })
public class UnboundTriggerHandlerCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoTrigger trigger : app.getTriggers()) {
            for (FlogoHandler handler : trigger.getHandlers()) {
                String flowURI = handler.getFlowURI();
                if (flowURI == null || flowURI.trim().isEmpty()) {
                    String handlerName = handler.getName() != null ? handler.getName() : "(unnamed)";
                    String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
                    addIssue("Handler '" + handlerName + "' on trigger '" + triggerName
                            + "' has no flow binding (flowURI is empty). Every handler must be bound to a flow.",
                            FlogoParser.findElementLine(app.getRawContent(),
                                    handlerName.equals("(unnamed)") ? triggerName : handlerName));
                }
            }
        }
    }
}
