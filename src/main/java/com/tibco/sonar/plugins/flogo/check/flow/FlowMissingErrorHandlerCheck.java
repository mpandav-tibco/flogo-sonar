package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "FlowMissingErrorHandler", name = "Flow should have error handling", priority = Priority.MAJOR, tags = {
        "error-handling", "reliability" })
public class FlowMissingErrorHandlerCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Check if flow has an explicit errorHandler block (parsed from JSON)
        if (flow.hasErrorHandler())
            return;

        // Check if flow has any error handler links (type = "error")
        boolean hasErrorHandling = flow.getLinks().stream()
                .anyMatch(l -> "error".equalsIgnoreCase(l.getType()));
        boolean hasErrorTask = flow.getTasks().stream()
                .anyMatch(t -> {
                    String id = t.getId() != null ? t.getId().toLowerCase() : "";
                    return id.contains("error") || id.contains("catch") || id.contains("exception");
                });
        // Check metadata for errorHandler
        boolean hasErrorMeta = flow.getMetadata() != null && flow.getMetadata().containsKey("errorHandler");

        if (!hasErrorHandling && !hasErrorTask && !hasErrorMeta) {
            addIssue("Flow '" + flow.getName()
                    + "' has no error handling. Add error handler tasks or error links to handle failures gracefully.",
                    flowLine(app, flow));
        }
    }
}
