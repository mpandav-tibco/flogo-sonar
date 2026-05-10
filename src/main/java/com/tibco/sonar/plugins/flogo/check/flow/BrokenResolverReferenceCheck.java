package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.*;
import java.util.regex.*;

@Rule(key = "BrokenResolverReference", name = "Mapping references non-existent activity or flow variable", priority = Priority.CRITICAL, tags = {
        "bug", "reliability" })
public class BrokenResolverReferenceCheck extends AbstractFlowCheck {

    // Matches $activity[TaskName].output.xxx or $activity[TaskName].xxx
    private static final Pattern ACTIVITY_REF = Pattern.compile("\\$activity\\[([^\\]]+)\\]");
    // Matches $flow.xxx references for flow-level inputs
    private static final Pattern FLOW_REF = Pattern.compile("\\$flow\\.([a-zA-Z_][a-zA-Z0-9_]*)");

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Build set of all task IDs in this flow
        Set<String> taskIds = new HashSet<>();
        for (FlogoTask task : flow.getTasks()) {
            taskIds.add(task.getId());
        }

        // Build set of flow metadata input names
        Set<String> flowInputs = new HashSet<>();
        Map<String, Object> metadata = flow.getMetadata();
        if (metadata != null) {
            Object input = metadata.get("input");
            if (input instanceof List) {
                for (Object item : (List<?>) input) {
                    if (item instanceof Map) {
                        Object name = ((Map<?, ?>) item).get("name");
                        if (name != null)
                            flowInputs.add(name.toString());
                    }
                }
            }
        }

        // Check every task's input mappings for broken $activity[X] references
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;

            Map<String, Object> inputs = task.getActivity().getInput();
            if (inputs == null)
                continue;

            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                if (entry.getValue() == null)
                    continue;
                String val = entry.getValue().toString();

                // Check $activity[X] references
                Matcher matcher = ACTIVITY_REF.matcher(val);
                while (matcher.find()) {
                    String refId = matcher.group(1);
                    if (!taskIds.contains(refId)) {
                        addIssue("Activity '" + task.getId() + "' in flow '" + flow.getName()
                                + "' references $activity[" + refId + "] in input '" + entry.getKey()
                                + "', but no task with ID '" + refId + "' exists in this flow.",
                                taskLine(app, task.getId()));
                    }
                }
            }
        }
    }
}
