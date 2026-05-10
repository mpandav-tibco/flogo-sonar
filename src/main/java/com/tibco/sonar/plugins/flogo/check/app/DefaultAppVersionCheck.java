package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "DefaultAppVersion", name = "Application should not use default version", priority = Priority.MINOR, tags = {
        "convention" })
public class DefaultAppVersionCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        if ("1.0.0".equals(app.getVersion())) {
            addIssue("Application '" + app.getName()
                    + "' is still using the default version '1.0.0'. Update the version to reflect actual releases.",
                    1);
        }
    }
}
