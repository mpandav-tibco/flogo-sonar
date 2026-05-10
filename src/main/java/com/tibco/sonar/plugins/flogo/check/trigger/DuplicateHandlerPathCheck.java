package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.*;

@Rule(key = "DuplicateHandlerPath", name = "Duplicate HTTP method and path on same trigger", priority = Priority.CRITICAL, tags = {
        "bug", "reliability" })
public class DuplicateHandlerPathCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isRestTrigger())
            return;

        Set<String> seen = new HashSet<>();
        for (FlogoHandler handler : trigger.getHandlers()) {
            String method = handler.getMethod();
            String path = handler.getPath();
            if (method != null && path != null) {
                String key = method.toUpperCase() + " " + path;
                if (!seen.add(key)) {
                    String handlerName = handler.getName() != null ? handler.getName() : key;
                    addIssue("Duplicate handler for '" + key + "' on trigger '"
                            + (trigger.getName() != null ? trigger.getName() : trigger.getId())
                            + "'. Each method+path combination should be unique.",
                            triggerLine(app, trigger));
                }
            }
        }
    }
}
