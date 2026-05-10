package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "InsecurePropertyURL", name = "Property values should not use HTTP URLs", priority = Priority.MAJOR, tags = {
        "security", "owasp-a02" })
public class InsecurePropertyURLCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoProperty prop : app.getProperties()) {
            String val = prop.getValueAsString();
            if (val != null && val.toLowerCase().startsWith("http://")) {
                addIssue("Property '" + prop.getName()
                        + "' uses insecure HTTP URL: " + truncate(val)
                        + ". Use HTTPS for secure communication.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"name\": \"" + prop.getName() + "\""));
            }
        }
    }

    private String truncate(String val) {
        return val.length() > 60 ? val.substring(0, 60) + "..." : val;
    }
}
