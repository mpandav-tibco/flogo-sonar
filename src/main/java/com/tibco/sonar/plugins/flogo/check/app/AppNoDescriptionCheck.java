package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "AppNoDescription", name = "Application should have a description", priority = Priority.MINOR, tags = {
        "convention", "documentation" })
public class AppNoDescriptionCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        if (app.getDescription() == null || app.getDescription().trim().isEmpty()) {
            addIssue("Flogo application '" + app.getName() + "' has no description.", 1);
        }
    }
}
