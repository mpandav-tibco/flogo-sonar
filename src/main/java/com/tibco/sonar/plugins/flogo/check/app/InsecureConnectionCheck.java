package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "InsecureConnection", name = "Connections should not use insecure HTTP scheme", priority = Priority.MAJOR, tags = {
        "security", "owasp-a02" })
public class InsecureConnectionCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (var entry : app.getConnections().entrySet()) {
            FlogoConnection conn = entry.getValue();
            String scheme = conn.getSettingAsString("scheme");
            if ("http".equalsIgnoreCase(scheme)) {
                addIssue("Connection '" + entry.getKey() + "' uses HTTP scheme. Use HTTPS for secure communication.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"" + entry.getKey() + "\""));
            }
        }
    }
}
