package com.tibco.sonar.plugins.flogo;

import org.sonar.api.Plugin;
import com.tibco.sonar.plugins.flogo.language.FlogoLanguage;
import com.tibco.sonar.plugins.flogo.sensor.FlogoSensor;
import com.tibco.sonar.plugins.flogo.rulerepository.FlogoRuleDefinition;
import com.tibco.sonar.plugins.flogo.profile.FlogoQualityProfile;
import com.tibco.sonar.plugins.flogo.metric.FlogoMetrics;
import com.tibco.sonar.plugins.flogo.settings.FlogoSettings;

public class FlogoPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(
                FlogoLanguage.class,
                FlogoQualityProfile.class,
                FlogoRuleDefinition.class,
                FlogoSensor.class,
                FlogoMetrics.class);
        context.addExtensions(FlogoSettings.getProperties());
    }
}
