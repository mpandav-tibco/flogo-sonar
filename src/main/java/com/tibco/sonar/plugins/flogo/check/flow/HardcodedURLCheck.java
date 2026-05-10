package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.regex.Pattern;

@Rule(key = "HardcodedURL", name = "URLs should use application properties", priority = Priority.MAJOR, tags = {
        "bad-practice", "maintainability" })
public class HardcodedURLCheck extends AbstractFlowCheck {

    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\"\\s]+", Pattern.CASE_INSENSITIVE);

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            FlogoActivity activity = task.getActivity();

            // Check REST activity URI
            if (activity.isRestRef()) {
                checkValue(activity.getInputValue("Uri"), task, flow, app, "URI");
                checkValue(activity.getInputValue("url"), task, flow, app, "URL");
            }

            // Check settings for hardcoded URLs
            if (activity.getSettings() != null) {
                for (var entry : activity.getSettings().entrySet()) {
                    if (entry.getValue() instanceof String val) {
                        if (isHardcodedURL(val)) {
                            addIssue(
                                    "Task '" + task.getId() + "' in flow '" + flow.getName()
                                            + "' has hardcoded URL in setting '" + entry.getKey() + "': "
                                            + truncate(val) + ". Use $property[...] instead.",
                                    taskLine(app, task.getId()));
                        }
                    }
                }
            }
        }
    }

    private void checkValue(Object value, FlogoTask task, FlogoFlow flow, FlogoApp app, String field) {
        if (value instanceof String val) {
            if (isHardcodedURL(val)) {
                addIssue(
                        "Task '" + task.getId() + "' in flow '" + flow.getName() + "' has hardcoded " + field + ": "
                                + truncate(val) + ". Use $property[...] instead.",
                        taskLine(app, task.getId()));
            }
        }
    }

    private boolean isHardcodedURL(String value) {
        if (value == null)
            return false;
        // Skip expression mappings (any value starting with '=' is a Flogo expression)
        if (value.startsWith("="))
            return false;
        return URL_PATTERN.matcher(value).find();
    }

    private String truncate(String val) {
        return val.length() > 60 ? val.substring(0, 60) + "..." : val;
    }
}
