package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;
import java.util.Map;

@Rule(key = "ComplexMappingExpression", name = "Mapping expressions should not be overly complex", priority = Priority.MINOR, tags = {
        "complexity", "maintainability" })
public class ComplexMappingExpressionCheck extends AbstractFlowCheck {

    @RuleProperty(key = "maxLength", description = "Maximum expression length", defaultValue = "500")
    private int maxLength = 500;

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            checkMapValues(task.getActivity().getInput(), task, flow, app);
        }
    }

    private void checkMapValues(Map<String, Object> map, FlogoTask task, FlogoFlow flow, FlogoApp app) {
        if (map == null)
            return;
        for (var entry : map.entrySet()) {
            if (entry.getValue() instanceof String val) {
                if (val.startsWith("=") && val.length() > maxLength) {
                    addIssue(
                            "Task '" + task.getId() + "' in flow '" + flow.getName()
                                    + "' has a complex mapping expression (" + val.length() + " chars) in field '"
                                    + entry.getKey() + "'. Consider using a subflow or intermediate variables.",
                            taskLine(app, task.getId()));
                }
            }
        }
    }
}
