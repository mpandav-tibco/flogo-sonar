package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "ConnectionNoTLS", name = "Connections should use TLS/SSL", priority = Priority.MAJOR, tags = { "security",
        "owasp-a02" })
public class ConnectionNoTLSCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        if (app.getConnections() == null)
            return;
        for (var entry : app.getConnections().entrySet()) {
            FlogoConnection conn = entry.getValue();
            boolean useTLS = conn.getSettingAsBoolean("useTLS");
            String scheme = conn.getSettingAsString("scheme");
            if (!useTLS && (scheme == null || "http".equalsIgnoreCase(scheme))) {
                addIssue("Connection '" + entry.getKey()
                        + "' does not use TLS (useTLS=false, scheme=http). Enable TLS for secure communication.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"" + entry.getKey() + "\""));
            }
        }
    }
}
