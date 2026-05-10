package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.regex.Pattern;

@Rule(key = "FlowNamingConvention", name = "Flow names should follow naming conventions", priority = Priority.MINOR, tags = {
        "convention" })
public class FlowNamingConventionCheck extends AbstractFlowCheck {

    private static final Pattern VALID_NAME = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        String name = flow.getName();
        if (name != null && !VALID_NAME.matcher(name).matches()) {
            addIssue("Flow name '" + name
                    + "' does not follow snake_case convention (expected pattern: lowercase letters, digits, underscores).",
                    flowLine(app, flow));
        }
    }
}
