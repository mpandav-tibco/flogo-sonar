package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.regex.Pattern;

@Rule(key = "PropertyNamingConvention", name = "Property names should follow naming conventions", priority = Priority.MINOR, tags = {
        "convention" })
public class PropertyNamingConventionCheck extends AbstractAppCheck {

    private static final Pattern VALID_PROPERTY = Pattern.compile("^[A-Z][A-Z0-9_]*$|^[a-zA-Z][a-zA-Z0-9_.]*$");

    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoProperty prop : app.getProperties()) {
            String name = prop.getName();
            if (name == null)
                continue;
            // Skip auto-generated connection binding properties (contain =$property[)
            if (name.contains("=$property["))
                continue;
            if (!VALID_PROPERTY.matcher(name).matches()) {
                addIssue("Property '" + name
                        + "' does not follow naming conventions. Use UPPER_SNAKE_CASE or dotted.camelCase notation.",
                        FlogoParser.findLineNumber(app.getRawContent(), "\"name\": \"" + name + "\""));
            }
        }
    }
}
