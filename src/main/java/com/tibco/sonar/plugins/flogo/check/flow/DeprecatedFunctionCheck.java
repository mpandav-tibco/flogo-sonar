package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.*;

@Rule(key = "DeprecatedFunction", name = "Mapping uses deprecated function", priority = Priority.MAJOR, tags = {
        "reliability", "maintainability" })
public class DeprecatedFunctionCheck extends AbstractFlowCheck {

    private static final Map<String, String> DEPRECATED = new LinkedHashMap<>();
    static {
        DEPRECATED.put("string.tostring(", "coerce.toString()");
        DEPRECATED.put("string.integer(", "coerce.toInt()");
        DEPRECATED.put("string.float(", "coerce.toFloat64()");
    }

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            checkMap(task.getActivity().getInput(), task, flow, app);
            checkMap(task.getActivity().getSettings(), task, flow, app);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkMap(Map<String, Object> map, FlogoTask task, FlogoFlow flow, FlogoApp app) {
        if (map == null)
            return;
        for (var entry : map.entrySet()) {
            if (entry.getValue() == null)
                continue;
            if (entry.getValue() instanceof Map) {
                checkMap((Map<String, Object>) entry.getValue(), task, flow, app);
                continue;
            }
            String val = entry.getValue().toString();
            if (!val.startsWith("="))
                continue;
            String lower = val.toLowerCase();
            for (var dep : DEPRECATED.entrySet()) {
                if (lower.contains(dep.getKey())) {
                    addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                            + "' uses deprecated function '" + dep.getKey().replace("(", "")
                            + "' in field '" + entry.getKey()
                            + "'. Use '" + dep.getValue() + "' instead.",
                            taskLine(app, task.getId()));
                }
            }
        }
    }
}
