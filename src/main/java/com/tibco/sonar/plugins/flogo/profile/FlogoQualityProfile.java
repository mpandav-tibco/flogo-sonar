package com.tibco.sonar.plugins.flogo.profile;

import com.tibco.sonar.plugins.flogo.language.FlogoLanguage;
import com.tibco.sonar.plugins.flogo.rulerepository.FlogoRuleDefinition;
import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.check.Rule;

public class FlogoQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Flogo Way";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, FlogoLanguage.KEY);
        profile.setDefault(true);

        for (Class<? extends AbstractFlogoCheck> checkClass : FlogoRuleDefinition.getCheckClasses()) {
            Rule annotation = checkClass.getAnnotation(Rule.class);
            if (annotation != null) {
                profile.activateRule(FlogoRuleDefinition.REPOSITORY_KEY, annotation.key());
            }
        }

        profile.done();
    }
}
