package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = "OrphanHandler", name = "Trigger handler references non-existent flow", priority = Priority.CRITICAL, tags = {
        "reliability", "bug" })
public class OrphanHandlerCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        Set<String> flowIds = app.getFlows().stream()
                .map(FlogoFlow::getId)
                .collect(Collectors.toSet());

        for (FlogoTrigger trigger : app.getTriggers()) {
            for (FlogoHandler handler : trigger.getHandlers()) {
                String flowURI = handler.getFlowURI();
                if (flowURI != null) {
                    // flowURI format: "res://flow:flow_name"
                    String flowId = flowURI.replace("res://", "");
                    if (!flowIds.contains(flowId)) {
                        String handlerName = handler.getName() != null ? handler.getName() : flowURI;
                        addIssue("Handler '" + handlerName + "' references non-existent flow '" + flowURI
                                + "'. Ensure the flow exists or remove the handler.",
                                FlogoParser.findElementLine(app.getRawContent(), handlerName));
                    }
                }
            }
        }
    }
}
