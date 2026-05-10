package com.tibco.sonar.plugins.flogo.check;

import com.tibco.sonar.plugins.flogo.model.FlogoApp;
import java.util.*;

/**
 * Base class for all Flogo quality checks.
 */
public abstract class AbstractFlogoCheck {

    private final List<FlogoIssue> issues = new ArrayList<>();

    /**
     * Run the check against the given Flogo application model.
     */
    public abstract void validate(FlogoApp app);

    /**
     * Report an issue found during validation.
     */
    protected void addIssue(String message, int line) {
        issues.add(new FlogoIssue(message, line));
    }

    /**
     * Report an issue at line 1 (file-level).
     */
    protected void addIssue(String message) {
        addIssue(message, 1);
    }

    public List<FlogoIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public void clearIssues() {
        issues.clear();
    }

    /**
     * Get the rule key from the @Rule annotation.
     */
    public String getRuleKey() {
        org.sonar.check.Rule annotation = getClass().getAnnotation(org.sonar.check.Rule.class);
        return annotation != null ? annotation.key() : getClass().getSimpleName();
    }

    /**
     * Represents a single issue found by a check.
     */
    public static class FlogoIssue {
        private final String message;
        private final int line;

        public FlogoIssue(String message, int line) {
            this.message = message;
            this.line = line;
        }

        public String getMessage() {
            return message;
        }

        public int getLine() {
            return line;
        }
    }
}
