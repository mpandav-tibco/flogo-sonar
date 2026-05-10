package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "DisabledSSLVerification", name = "SSL verification should not be disabled", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a02" })
public class DisabledSSLVerificationCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        String raw = app.getRawContent();
        if (raw == null)
            return;
        // Check for disableSSLVerification or tlsInsecureSkipVerify in raw content
        if (raw.contains("\"disableSSLVerification\": true") || raw.contains("\"disableSSLVerification\":true")) {
            addIssue(
                    "SSL verification is disabled (disableSSLVerification=true). This makes the application vulnerable to man-in-the-middle attacks.",
                    FlogoParser.findLineNumber(raw, "disableSSLVerification"));
        }
        if (raw.contains("\"tlsInsecureSkipVerify\": true") || raw.contains("\"tlsInsecureSkipVerify\":true")) {
            addIssue(
                    "TLS certificate verification is skipped (tlsInsecureSkipVerify=true). This makes the application vulnerable to man-in-the-middle attacks.",
                    FlogoParser.findLineNumber(raw, "tlsInsecureSkipVerify"));
        }
        // Check skipSSLVerify / skipTlsVerify (custom extensions pattern)
        if (raw.contains("\"skipSSLVerify\": true") || raw.contains("\"skipSSLVerify\":true")
                || raw.contains("\"skipSSLVerify\": \"True\"") || raw.contains("\"skipSSLVerify\":\"True\"")) {
            addIssue(
                    "SSL verification is skipped (skipSSLVerify=true). This makes the application vulnerable to man-in-the-middle attacks.",
                    FlogoParser.findLineNumber(raw, "skipSSLVerify"));
        }
        if (raw.contains("\"skipTlsVerify\": true") || raw.contains("\"skipTlsVerify\":true")) {
            addIssue(
                    "TLS verification is skipped (skipTlsVerify=true). This makes the application vulnerable to man-in-the-middle attacks.",
                    FlogoParser.findLineNumber(raw, "skipTlsVerify"));
        }
        // Check sslMode: disable (DB triggers pattern)
        if (raw.contains("\"sslMode\": \"disable\"") || raw.contains("\"sslMode\":\"disable\"")) {
            addIssue(
                    "SSL mode is disabled (sslMode=disable). Database connections should use 'require', 'verify-ca', or 'verify-full' to encrypt traffic.",
                    FlogoParser.findLineNumber(raw, "sslMode"));
        }
    }
}
