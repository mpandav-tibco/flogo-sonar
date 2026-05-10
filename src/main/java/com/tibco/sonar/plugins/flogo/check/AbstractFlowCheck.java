package com.tibco.sonar.plugins.flogo.check;

import com.tibco.sonar.plugins.flogo.model.FlogoApp;
import com.tibco.sonar.plugins.flogo.model.FlogoFlow;
import com.tibco.sonar.plugins.flogo.model.FlogoParser;

/**
 * Base class for checks that operate on individual flows.
 */
public abstract class AbstractFlowCheck extends AbstractFlogoCheck {

    @Override
    public void validate(FlogoApp app) {
        for (FlogoFlow flow : app.getFlows()) {
            validateFlow(flow, app);
        }
    }

    protected abstract void validateFlow(FlogoFlow flow, FlogoApp app);

    /**
     * Find the line number of a flow in the raw content.
     */
    protected int flowLine(FlogoApp app, FlogoFlow flow) {
        return FlogoParser.findElementLine(app.getRawContent(), flow.getName());
    }

    /**
     * Find the line number of a task in the raw content.
     */
    protected int taskLine(FlogoApp app, String taskId) {
        return FlogoParser.findElementLine(app.getRawContent(), taskId);
    }
}
