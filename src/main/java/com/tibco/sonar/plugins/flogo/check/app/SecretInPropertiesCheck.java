package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.regex.Pattern;

@Rule(key = "SecretInProperties", name = "Application properties should not contain plaintext secrets", priority = Priority.BLOCKER, tags = {
        "security", "cwe", "owasp-a07" })
public class SecretInPropertiesCheck extends AbstractAppCheck {

    private static final Pattern SECRET_NAMES = Pattern.compile(
            "password|passwd|secret|api.?key|access.?key|secret.?key|session.?token|token|private.?key|credential",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoProperty prop : app.getProperties()) {
            if (SECRET_NAMES.matcher(prop.getName()).find()) {
                String val = prop.getValueAsString();
                if (!val.isEmpty() && !val.startsWith("SECRET:")
                        && !val.startsWith("=")
                        && !val.startsWith("$property[")
                        && !val.startsWith("$env[")) {
                    addIssue("Property '" + prop.getName()
                            + "' appears to contain a plaintext secret. Use encrypted secrets (SECRET:...) or environment variable references.",
                            FlogoParser.findLineNumber(app.getRawContent(), "\"name\": \"" + prop.getName() + "\""));
                }
            }
        }
    }
}
