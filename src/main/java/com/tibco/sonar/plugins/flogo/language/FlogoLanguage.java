package com.tibco.sonar.plugins.flogo.language;

import com.tibco.sonar.plugins.flogo.settings.FlogoSettings;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class FlogoLanguage extends AbstractLanguage {

    public static final String KEY = "flogo";
    public static final String NAME = "Flogo";
    public static final String[] DEFAULT_FILE_SUFFIXES = { ".flogo" };

    private final Configuration configuration;

    public FlogoLanguage(Configuration configuration) {
        super(KEY, NAME);
        this.configuration = configuration;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = configuration.getStringArray(FlogoSettings.FLOGO_FILE_SUFFIXES_KEY);
        if (suffixes != null && suffixes.length > 0) {
            return suffixes;
        }
        return DEFAULT_FILE_SUFFIXES;
    }
}
