package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Map;

@Rule(key = "MD5HashFunction", name = "Mapping uses MD5 hash which is cryptographically broken", priority = Priority.MAJOR, tags = {
        "security", "owasp-a02" })
public class MD5HashFunctionCheck extends AbstractFlowCheck {

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
            if (val.contains("util.md5(")) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                        + "' uses util.md5() in field '" + entry.getKey()
                        + "'. MD5 is cryptographically broken — use util.sha256() instead.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
