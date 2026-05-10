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

    protected int countOccurrences(String text, String search) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(search, idx)) != -1) {
            count++;
            idx += search.length();
        }
        return count;
    }
}
