package com.tibco.sonar.plugins.flogo.language;

import org.sonar.api.resources.AbstractLanguage;

public class FlogoLanguage extends AbstractLanguage {

    public static final String KEY = "flogo";
    public static final String NAME = "Flogo";
    public static final String[] FILE_SUFFIXES = { ".flogo" };

    public FlogoLanguage() {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return FILE_SUFFIXES;
    }
}
