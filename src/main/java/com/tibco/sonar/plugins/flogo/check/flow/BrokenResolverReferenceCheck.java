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

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Build set of all task IDs in this flow
        Set<String> taskIds = new HashSet<>();
        for (FlogoTask task : flow.getTasks()) {
            taskIds.add(task.getId());
        }

        // Check every task's input mappings for broken $activity[X] references
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;

            checkMapForBrokenRefs(task.getActivity().getInput(), task, flow, app, taskIds);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkMapForBrokenRefs(Map<String, Object> map, FlogoTask task, FlogoFlow flow, FlogoApp app,
            Set<String> taskIds) {
        if (map == null)
            return;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null)
                continue;

            if (entry.getValue() instanceof String val) {
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
            } else if (entry.getValue() instanceof Map) {
                checkMapForBrokenRefs((Map<String, Object>) entry.getValue(), task, flow, app, taskIds);
            }
        }
    }
}
