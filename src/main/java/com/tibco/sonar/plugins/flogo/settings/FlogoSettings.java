package com.tibco.sonar.plugins.flogo.settings;

import org.sonar.api.config.PropertyDefinition;
import java.util.*;

public class FlogoSettings {

        public static final String FLOGO_FILE_SUFFIXES_KEY = "sonar.flogo.file.suffixes";
        public static final String FLOGO_FILE_SUFFIXES_DEFAULT = ".flogo";

        private FlogoSettings() {
        }

        public static List<PropertyDefinition> getProperties() {
                return Collections.singletonList(
                                PropertyDefinition.builder(FLOGO_FILE_SUFFIXES_KEY)
                                                .name("File Suffixes")
                                                .description("Comma-separated list of suffixes of Flogo files to analyze")
                                                .defaultValue(FLOGO_FILE_SUFFIXES_DEFAULT)
                                                .category("Flogo")
                                                .multiValues(true)
                                                .build());
        }
}
