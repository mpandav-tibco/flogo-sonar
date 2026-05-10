package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "GrpcNoTLS", name = "gRPC trigger or activity should use TLS", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a02" })
public class GrpcNoTLSCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!trigger.isGrpcTrigger())
            return;

        Object enableTLS = trigger.getSetting("enableTLS");
        if (enableTLS == null || "false".equals(String.valueOf(enableTLS))) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue("gRPC trigger '" + triggerName
                    + "' does not use TLS. Enable TLS (enableTLS=true) to encrypt gRPC communication.",
                    triggerLine(app, trigger));
        }
    }
}
