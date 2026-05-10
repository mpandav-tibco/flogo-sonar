package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Arrays;
import java.util.List;

@Rule(key = "ConnectionCredential", name = "Connection has plaintext credentials", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a2" })
public class ConnectionCredentialCheck extends AbstractAppCheck {

    private static final List<String> CREDENTIAL_KEYS = Arrays.asList(
            "password", "apiKey", "api_key", "secret", "token",
            "accessToken", "access_token", "accessKey", "access_key",
            "secretKey", "secret_key", "sessionToken", "session_token",
            "clientSecret", "client_secret",
            "privateKey", "private_key");

    @Override
    protected void validateApp(FlogoApp app) {
        if (app.getConnections() == null)
            return;

        for (FlogoConnection conn : app.getConnections().values()) {
            for (String key : CREDENTIAL_KEYS) {
                String val = conn.getSettingAsString(key);
                if (val != null && !val.isEmpty()
                        && !val.startsWith("$property[")
                        && !val.startsWith("$env[")
                        && !val.startsWith("SECRET:")
                        && !val.startsWith("=")) {
                    addIssue("Connection '" + conn.getName() + "' has plaintext value for setting '" + key
                            + "'. Use $property[\"..\"] or $env[\"..\"] references instead.",
                            FlogoParser.findElementLine(app.getRawContent(), conn.getName()));
                }
            }
        }
    }
}
