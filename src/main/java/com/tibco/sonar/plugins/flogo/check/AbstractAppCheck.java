package com.tibco.sonar.plugins.flogo.check;

import com.tibco.sonar.plugins.flogo.model.FlogoApp;

/**
 * Base class for checks that operate on the entire app/project.
 */
public abstract class AbstractAppCheck extends AbstractFlogoCheck {

    @Override
    public void validate(FlogoApp app) {
        validateApp(app);
    }

    protected abstract void validateApp(FlogoApp app);
}
