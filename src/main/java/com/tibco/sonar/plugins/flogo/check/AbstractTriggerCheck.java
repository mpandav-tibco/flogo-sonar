package com.tibco.sonar.plugins.flogo.check;

import com.tibco.sonar.plugins.flogo.model.FlogoApp;
import com.tibco.sonar.plugins.flogo.model.FlogoTrigger;
import com.tibco.sonar.plugins.flogo.model.FlogoParser;

/**
 * Base class for checks that operate on triggers.
 */
public abstract class AbstractTriggerCheck extends AbstractFlogoCheck {

    @Override
    public void validate(FlogoApp app) {
        for (FlogoTrigger trigger : app.getTriggers()) {
            validateTrigger(trigger, app);
        }
    }

    protected abstract void validateTrigger(FlogoTrigger trigger, FlogoApp app);

    protected int triggerLine(FlogoApp app, FlogoTrigger trigger) {
        String name = trigger.getId() != null ? trigger.getId() : trigger.getName();
        return FlogoParser.findElementLine(app.getRawContent(), name);
    }
}
