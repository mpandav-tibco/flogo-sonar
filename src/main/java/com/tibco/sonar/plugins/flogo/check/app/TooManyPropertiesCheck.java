package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;

@Rule(key = "TooManyProperties", name = "Application should not have too many properties", priority = Priority.MINOR, tags = {
        "complexity", "maintainability" })
public class TooManyPropertiesCheck extends AbstractAppCheck {

    @RuleProperty(key = "maxProperties", description = "Maximum app properties", defaultValue = "50")
    private int maxProperties = 50;

    @Override
    protected void validateApp(FlogoApp app) {
        int count = app.getProperties().size();
        if (count > maxProperties) {
            addIssue("Application has " + count + " properties (max " + maxProperties
                    + "). Consider grouping related properties or using configuration files.", 1);
        }
    }
}
