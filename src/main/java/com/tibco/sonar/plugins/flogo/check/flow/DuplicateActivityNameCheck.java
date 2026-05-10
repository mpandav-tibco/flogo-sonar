package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "DuplicateActivityName", name = "Activity names should be unique within a flow", priority = Priority.MAJOR, tags = {
        "bug", "convention" })
public class DuplicateActivityNameCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        Map<String, Integer> nameCount = new HashMap<>();
        for (FlogoTask task : flow.getTasks()) {
            String name = task.getName();
            if (name != null) {
                nameCount.merge(name, 1, Integer::sum);
            }
        }
        for (var entry : nameCount.entrySet()) {
            if (entry.getValue() > 1) {
                addIssue(
                        "Activity name '" + entry.getKey() + "' appears " + entry.getValue() + " times in flow '"
                                + flow.getName() + "'. Use unique names for clarity.",
                        flowLine(app, flow));
            }
        }
    }
}
