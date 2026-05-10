package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Set;
import java.util.HashSet;

@Rule(key = "UnusedFlow", name = "Flow is not referenced by any trigger handler", priority = Priority.MINOR, tags = {
        "maintainability", "dead-code" })
public class UnusedFlowCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        // Collect all flow URIs referenced by trigger handlers
        Set<String> referencedFlows = new HashSet<>();
        for (FlogoTrigger trigger : app.getTriggers()) {
            for (FlogoHandler handler : trigger.getHandlers()) {
                String flowURI = handler.getFlowURI();
                if (flowURI != null) {
                    // flowURI format: "res://flow:flow_name"
                    referencedFlows.add(flowURI.replace("res://", ""));
                }
            }
        }

        // Also collect subflow references from flow tasks
        for (FlogoFlow flow : app.getFlows()) {
            for (FlogoTask task : flow.getTasks()) {
                if (task.getActivity() != null && task.getActivity().isSubflowRef()) {
                    String subflowURI = task.getActivity().getSettingAsString("flowURI");
                    if (subflowURI != null) {
                        referencedFlows.add(subflowURI.replace("res://", ""));
                    }
                }
            }
        }

        // Check which flows are not referenced
        for (FlogoFlow flow : app.getFlows()) {
            if (!referencedFlows.contains(flow.getId())) {
                addIssue("Flow '" + flow.getName()
                        + "' is not referenced by any trigger handler or subflow activity. "
                        + "Remove dead flows to reduce application complexity.",
                        FlogoParser.findElementLine(app.getRawContent(), flow.getName()));
            }
        }
    }
}
