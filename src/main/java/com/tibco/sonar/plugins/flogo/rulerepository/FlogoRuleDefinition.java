package com.tibco.sonar.plugins.flogo.rulerepository;

import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import com.tibco.sonar.plugins.flogo.check.flow.*;
import com.tibco.sonar.plugins.flogo.check.trigger.*;
import com.tibco.sonar.plugins.flogo.check.app.*;
import com.tibco.sonar.plugins.flogo.language.FlogoLanguage;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FlogoRuleDefinition implements RulesDefinition {

        private static final Logger LOG = LoggerFactory.getLogger(FlogoRuleDefinition.class);

        public static final String REPOSITORY_KEY = "flogo";
        public static final String REPOSITORY_NAME = "Flogo Rules";

        @Override
        public void define(Context context) {
                NewRepository repository = context.createRepository(REPOSITORY_KEY, FlogoLanguage.KEY);
                repository.setName(REPOSITORY_NAME);

                RulesDefinitionAnnotationLoader loader = new RulesDefinitionAnnotationLoader();
                loader.load(repository, getCheckClasses().toArray(new Class[0]));

                // Load HTML descriptions from classpath resources
                for (NewRule rule : repository.rules()) {
                        String desc = loadDescription(rule.key());
                        if (desc != null) {
                                rule.setHtmlDescription(desc);
                        }
                }

                repository.done();
        }

        private String loadDescription(String ruleKey) {
                String path = "/rules/" + ruleKey + ".html";
                try (InputStream is = getClass().getResourceAsStream(path)) {
                        if (is == null) {
                                LOG.warn("No description resource found for rule: {}", ruleKey);
                                return null;
                        }
                        return new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                } catch (IOException e) {
                        LOG.warn("Error reading description for rule {}: {}", ruleKey, e.getMessage());
                        return null;
                }
        }

        public static List<Class<? extends AbstractFlogoCheck>> getCheckClasses() {
                return Arrays.asList(
                                // Flow checks
                                FlowNoDescriptionCheck.class,
                                FlowMissingReturnCheck.class,
                                FlowDeadEndCheck.class,
                                FlowUnreachableTaskCheck.class,
                                FlowTooManyActivitiesCheck.class,
                                FlowMissingErrorHandlerCheck.class,
                                HardcodedURLCheck.class,
                                HardcodedCredentialsCheck.class,
                                EmptyActivityDescriptionCheck.class,
                                TransitionLabelCheck.class,
                                MultipleTransitionsNoConditionCheck.class,
                                DuplicateActivityNameCheck.class,
                                FlowNamingConventionCheck.class,
                                RestActivityTimeoutCheck.class,
                                NoRetryOnErrorCheck.class,
                                EmptyActivityInputCheck.class,
                                ComplexMappingExpressionCheck.class,
                                EmptyFlowCheck.class,
                                FlowComplexityCheck.class,
                                SensitiveDataInLogCheck.class,
                                FlowNoLoggingCheck.class,
                                SubflowNoTimeoutCheck.class,
                                SleepActivityCheck.class,
                                RestErrorSilencedCheck.class,
                                EmptyTransitionConditionCheck.class,
                                CircularLinkCheck.class,
                                BrokenResolverReferenceCheck.class,
                                DuplicateLinkCheck.class,
                                ActivitySkipTLSVerifyCheck.class,
                                DeprecatedFunctionCheck.class,
                                MD5HashFunctionCheck.class,
                                HardcodedCryptoKeyCheck.class,
                                RegexInjectionRiskCheck.class,
                                SQLConcatInMappingCheck.class,
                                // Trigger checks
                                TriggerNoAuthCheck.class,
                                TriggerInsecureHTTPCheck.class,
                                TriggerNoDescriptionCheck.class,
                                TriggerTooManyHandlersCheck.class,
                                DuplicateHandlerPathCheck.class,
                                GrpcNoTLSCheck.class,
                                GraphQLIntrospectionCheck.class,
                                TriggerDefaultPortCheck.class,
                                WebSocketNoTLSCheck.class,
                                CORSWildcardCheck.class,
                                TriggerHardcodedCredentialsCheck.class,
                                // App checks
                                AppNoDescriptionCheck.class,
                                SecretInPropertiesCheck.class,
                                UnusedPropertyCheck.class,
                                UnusedConnectionCheck.class,
                                ConnectionNoTLSCheck.class,
                                ConnectionTimeoutCheck.class,
                                DuplicateFlowNameCheck.class,
                                DefaultAppVersionCheck.class,
                                TooManyPropertiesCheck.class,
                                PropertyNamingConventionCheck.class,
                                DisabledSSLVerificationCheck.class,
                                InsecureConnectionCheck.class,
                                MissingImportCheck.class,
                                OrphanHandlerCheck.class,
                                ConnectionCredentialCheck.class,
                                InsecurePropertyURLCheck.class,
                                HardcodedPropertyURLCheck.class,
                                SendMailNoTLSCheck.class,
                                UnboundTriggerHandlerCheck.class,
                                UnusedFlowCheck.class,
                                // Test quality checks
                                TestCaseNoAssertionCheck.class,
                                TestCaseEmptyInputCheck.class,
                                FlowNoTestCaseCheck.class);
        }
}
