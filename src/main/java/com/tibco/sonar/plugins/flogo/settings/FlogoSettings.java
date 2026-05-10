package com.tibco.sonar.plugins.flogo.settings;

import org.sonar.api.config.PropertyDefinition;
import java.util.*;

public class FlogoSettings {

    public static final String FLOGO_FILE_SUFFIXES_KEY = "sonar.flogo.file.suffixes";
    public static final String FLOGO_FILE_SUFFIXES_DEFAULT = ".flogo";
    public static final String FLOGO_MAX_ACTIVITIES_KEY = "sonar.flogo.maxActivities";
    public static final String FLOGO_MAX_ACTIVITIES_DEFAULT = "20";
    public static final String FLOGO_MAX_PROPERTIES_KEY = "sonar.flogo.maxProperties";
    public static final String FLOGO_MAX_PROPERTIES_DEFAULT = "50";
    public static final String FLOGO_MAX_HANDLERS_KEY = "sonar.flogo.maxHandlers";
    public static final String FLOGO_MAX_HANDLERS_DEFAULT = "10";
    public static final String FLOGO_MAX_EXPRESSION_LENGTH_KEY = "sonar.flogo.maxExpressionLength";
    public static final String FLOGO_MAX_EXPRESSION_LENGTH_DEFAULT = "500";

    private FlogoSettings() {
    }

    public static List<PropertyDefinition> getProperties() {
        return Arrays.asList(
                PropertyDefinition.builder(FLOGO_FILE_SUFFIXES_KEY)
                        .name("File Suffixes")
                        .description("Comma-separated list of suffixes of Flogo files to analyze")
                        .defaultValue(FLOGO_FILE_SUFFIXES_DEFAULT)
                        .category("Flogo")
                        .build(),
                PropertyDefinition.builder(FLOGO_MAX_ACTIVITIES_KEY)
                        .name("Max Activities Per Flow")
                        .description("Maximum number of activities allowed in a single flow")
                        .defaultValue(FLOGO_MAX_ACTIVITIES_DEFAULT)
                        .category("Flogo")
                        .build(),
                PropertyDefinition.builder(FLOGO_MAX_PROPERTIES_KEY)
                        .name("Max App Properties")
                        .description("Maximum number of app properties allowed")
                        .defaultValue(FLOGO_MAX_PROPERTIES_DEFAULT)
                        .category("Flogo")
                        .build(),
                PropertyDefinition.builder(FLOGO_MAX_HANDLERS_KEY)
                        .name("Max Trigger Handlers")
                        .description("Maximum number of handlers per trigger")
                        .defaultValue(FLOGO_MAX_HANDLERS_DEFAULT)
                        .category("Flogo")
                        .build(),
                PropertyDefinition.builder(FLOGO_MAX_EXPRESSION_LENGTH_KEY)
                        .name("Max Expression Length")
                        .description("Maximum character length for mapping expressions before flagging complexity")
                        .defaultValue(FLOGO_MAX_EXPRESSION_LENGTH_DEFAULT)
                        .category("Flogo")
                        .build());
    }
}
