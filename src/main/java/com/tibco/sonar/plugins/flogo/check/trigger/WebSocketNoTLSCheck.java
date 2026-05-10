package com.tibco.sonar.plugins.flogo.check.trigger;

import com.tibco.sonar.plugins.flogo.check.AbstractTriggerCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "WebSocketNoTLS", name = "WebSocket trigger should use secure connection (WSS)", priority = Priority.MAJOR, tags = {
        "security", "owasp-a02" })
public class WebSocketNoTLSCheck extends AbstractTriggerCheck {
    @Override
    protected void validateTrigger(FlogoTrigger trigger, FlogoApp app) {
        if (!isWebSocketTrigger(trigger))
            return;

        Object secureConn = trigger.getSetting("secureConnection");
        Object enableTLS = trigger.getSetting("enableTLS");

        boolean isSecure = "true".equals(String.valueOf(secureConn))
                || "true".equals(String.valueOf(enableTLS));

        if (!isSecure) {
            String triggerName = trigger.getName() != null ? trigger.getName() : trigger.getId();
            addIssue("WebSocket trigger '" + triggerName
                    + "' does not use a secure connection (WSS). "
                    + "Enable secureConnection or enableTLS to encrypt WebSocket traffic.",
                    triggerLine(app, trigger));
        }
    }

    private boolean isWebSocketTrigger(FlogoTrigger trigger) {
        String ref = trigger.getRef();
        if (ref == null)
            return false;
        return ref.contains("websocket") || ref.contains("wsserver") || ref.contains("wsclient");
    }
}
