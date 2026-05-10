package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "UnusedProperty", name = "Application properties should be referenced", priority = Priority.MINOR, tags = {
        "dead-code", "maintainability" })
public class UnusedPropertyCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        String raw = app.getRawContent();
        if (raw == null)
            return;

        for (FlogoProperty prop : app.getProperties()) {
            String name = prop.getName();
            // Skip connector-generated properties (contain =$property[ or dots)
            if (name.contains(".") || name.contains("=$property["))
                continue;
            // In raw JSON, property references appear with escaped quotes:
            // =$property[\"PROP_NAME\"] or $property[\"PROP_NAME\"]
            String escapedRef = "$property[\\\"" + name + "\\\"]";
            String unescapedRef = "$property[\"" + name + "\"]";
            int count = countOccurrences(raw, escapedRef) + countOccurrences(raw, unescapedRef);
            if (count == 0) {
                addIssue(
                        "Property '" + name + "' is defined but never referenced via $property[\"" + name
                                + "\"]. Consider removing it.",
                        FlogoParser.findLineNumber(raw, "\"name\": \"" + name + "\""));
            }
        }
    }
}
