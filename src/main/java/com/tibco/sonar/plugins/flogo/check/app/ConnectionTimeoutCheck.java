package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "ConnectionTimeout", name = "Connections should have timeout configured", priority = Priority.MAJOR, tags = {
        "reliability", "performance" })
public class ConnectionTimeoutCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (var entry : app.getConnections().entrySet()) {
            FlogoConnection conn = entry.getValue();
            Object timeout = conn.getSetting("timeoutSeconds");
            if (timeout == null) {
                timeout = conn.getSetting("Timeout");
            }
            if (timeout == null || "0".equals(timeout.toString()) || "0.0".equals(timeout.toString())) {
                addIssue("Connection '" + entry.getKey()
                        + "' has no timeout or timeout=0. Configure a reasonable timeout to prevent hanging connections.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"" + entry.getKey() + "\""));
            }
        }
    }
}
