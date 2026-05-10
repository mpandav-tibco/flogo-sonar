package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.regex.Pattern;

@Rule(key = "HardcodedPropertyURL", name = "Property values should not contain hardcoded URLs", priority = Priority.MINOR, tags = {
        "bad-practice", "maintainability" })
public class HardcodedPropertyURLCheck extends AbstractAppCheck {

    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[^\"\\s]+", Pattern.CASE_INSENSITIVE);

    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoProperty prop : app.getProperties()) {
            String val = prop.getValueAsString();
            if (val != null && URL_PATTERN.matcher(val).matches()) {
                addIssue("Property '" + prop.getName()
                        + "' contains a hardcoded URL. Consider using environment variable references ($env[\"..\"]) "
                        + "for environment-specific URLs to support deployment across environments.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"name\": \"" + prop.getName() + "\""));
            }
        }
    }
}
