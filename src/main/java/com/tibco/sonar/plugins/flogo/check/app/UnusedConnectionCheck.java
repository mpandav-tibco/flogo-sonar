package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "UnusedConnection", name = "Connections should be referenced by activities", priority = Priority.MINOR, tags = {
        "dead-code", "maintainability" })
public class UnusedConnectionCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        String raw = app.getRawContent();
        if (raw == null)
            return;

        for (var entry : app.getConnections().entrySet()) {
            String connId = entry.getKey();
            String connRef = "conn://" + connId;
            // Check if any activity references this connection
            if (countOccurrences(raw, connRef) <= 1) {
                // Only defined once (in the connections block itself), never referenced
                addIssue(
                        "Connection '" + connId
                                + "' is defined but not referenced by any activity. Consider removing it.",
                        FlogoParser.findLineNumber(raw, "\"" + connId + "\""));
            }
        }
    }
}
