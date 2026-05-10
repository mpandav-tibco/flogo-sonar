package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Map;

@Rule(key = "RegexInjectionRisk", name = "Mapping uses regexExtract with potentially dynamic pattern", priority = Priority.MAJOR, tags = {
        "security", "owasp-a03" })
public class RegexInjectionRiskCheck extends AbstractFlowCheck {

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
            if (!val.contains("string.regexExtract("))
                continue;

            // Check if pattern argument references external input ($flow, $trigger,
            // $activity)
            // string.regexExtract(str, pattern) — pattern is the 2nd arg
            if (val.contains("$flow.") || val.contains("$trigger.") || val.contains("$env.")) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                        + "' uses string.regexExtract() with a dynamic pattern from external input in field '"
                        + entry.getKey()
                        + "'. Crafted patterns can cause excessive CPU usage (ReDoS). "
                        + "Consider using a hardcoded pattern or validating the input.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
