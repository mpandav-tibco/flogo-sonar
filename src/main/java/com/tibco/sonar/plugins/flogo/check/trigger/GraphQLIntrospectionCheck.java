package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "GraphQLIntrospection", name = "GraphQL introspection should be disabled in production", priority = Priority.MAJOR, tags = {
        "security", "owasp-a01" })
public class GraphQLIntrospectionCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isGraphQLTrigger())
            return;

        Object introspection = trigger.getSetting("introspection");
        // Default is true, so null or true means enabled
        if (introspection == null || "true".equals(String.valueOf(introspection))) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue("GraphQL trigger '" + triggerName
                    + "' has introspection enabled. Disable introspection in production to prevent schema exposure.",
                    triggerLine(app, trigger));
        }
    }
}
