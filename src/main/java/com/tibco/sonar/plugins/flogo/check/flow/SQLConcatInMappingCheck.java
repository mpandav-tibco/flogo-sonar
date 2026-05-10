package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Map;

@Rule(key = "SQLConcatInMapping", name = "String concatenation in database activity input may enable SQL injection", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a03" })
public class SQLConcatInMappingCheck extends AbstractFlowCheck {

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            String ref = task.getActivity().getRef();
            if (ref == null)
                continue;

            // Detect database-related activities by ref
            String refLower = ref.toLowerCase();
            boolean isDbActivity = refLower.contains("mysql") || refLower.contains("postgres")
                    || refLower.contains("sql") || refLower.contains("oracle")
                    || refLower.contains("jdbc") || refLower.contains("database")
                    || refLower.contains("db") || refLower.contains("mongo");

            if (!isDbActivity)
                continue;

            // Check inputs for string.concat() building queries
            Map<String, Object> inputs = task.getActivity().getInput();
            if (inputs == null)
                continue;

            for (var entry : inputs.entrySet()) {
                if (entry.getValue() == null)
                    continue;
                String val = entry.getValue().toString();
                if (!val.startsWith("="))
                    continue;

                String keyLower = entry.getKey().toLowerCase();
                boolean isSqlField = keyLower.contains("query") || keyLower.contains("sql")
                        || keyLower.contains("statement") || keyLower.contains("command");

                if (isSqlField && val.contains("string.concat(")) {
                    // Check if concat includes external input
                    if (val.contains("$flow.") || val.contains("$trigger.") || val.contains("$activity[")) {
                        addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                                + "' builds a SQL query using string.concat() with external input in field '"
                                + entry.getKey()
                                + "'. This is vulnerable to SQL injection. Use parameterized queries instead.",
                                taskLine(app, task.getId()));
                    }
                }
            }
        }
    }
}
