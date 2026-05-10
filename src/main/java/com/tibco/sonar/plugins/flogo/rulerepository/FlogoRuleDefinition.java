package com.tibco.sonar.plugins.flogo.rulerepository;

import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import com.tibco.sonar.plugins.flogo.check.flow.*;
import com.tibco.sonar.plugins.flogo.check.trigger.*;
import com.tibco.sonar.plugins.flogo.check.app.*;
import com.tibco.sonar.plugins.flogo.language.FlogoLanguage;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import java.util.*;

public class FlogoRuleDefinition implements RulesDefinition {

        public static final String REPOSITORY_KEY = "flogo";
        public static final String REPOSITORY_NAME = "Flogo Rules";

        @Override
        public void define(Context context) {
                NewRepository repository = context.createRepository(REPOSITORY_KEY, FlogoLanguage.KEY);
                repository.setName(REPOSITORY_NAME);

                RulesDefinitionAnnotationLoader loader = new RulesDefinitionAnnotationLoader();
                loader.load(repository, getCheckClasses().toArray(new Class[0]));

                // Set HTML descriptions for all rules
                for (NewRule rule : repository.rules()) {
                        String desc = getDescription(rule.key());
                        if (desc != null) {
                                rule.setHtmlDescription(desc);
                        }
                }

                repository.done();
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
                                UnusedFlowCheck.class);
        }

        private String getDescription(String ruleKey) {
                return DESCRIPTIONS.get(ruleKey);
        }

        private static final Map<String, String> DESCRIPTIONS = new LinkedHashMap<>();
        static {
                DESCRIPTIONS.put("FlowNoDescription",
                                "<p>Every flow should have a meaningful description explaining its purpose, inputs, and expected behavior. This improves maintainability and helps new team members understand the application.</p>");
                DESCRIPTIONS.put("FlowMissingReturn",
                                "<p>Flows should explicitly end with a Return (actreturn) activity to ensure proper response handling. Without a return activity, the flow may not send back expected results to the trigger.</p>");
                DESCRIPTIONS.put("FlowDeadEnd",
                                "<p>A task with no outgoing link (that is not a Return activity) creates a dead end in the flow. This usually indicates an incomplete flow design where execution stops unexpectedly.</p>");
                DESCRIPTIONS.put("FlowUnreachableTask",
                                "<p>Tasks that are not reachable from the Start activity represent dead code. They will never execute and should be removed or properly connected.</p>");
                DESCRIPTIONS.put("FlowTooManyActivities",
                                "<p>Flows with too many activities become hard to understand and maintain. Consider splitting complex flows into subflows for better organization and reusability.</p>");
                DESCRIPTIONS.put("FlowMissingErrorHandler",
                                "<p>Flows should have error handling to gracefully manage failures. Without error handling, exceptions may propagate uncontrolled and cause unexpected behavior.</p>");
                DESCRIPTIONS.put("HardcodedURL",
                                "<p>URLs should not be hardcoded in activity configurations. Use application properties ($property[\"...\"])) instead so URLs can be changed per environment without modifying the application.</p>");
                DESCRIPTIONS.put("HardcodedCredentials",
                                "<p>Credentials (passwords, API keys, tokens) must never be hardcoded. Use application properties with secrets management (SECRET:...) or environment variable references.</p>");
                DESCRIPTIONS.put("EmptyActivityDescription",
                                "<p>Activities should have descriptions explaining what they do. This makes flows easier to understand and maintain.</p>");
                DESCRIPTIONS.put("TransitionLabel",
                                "<p>Links between activities should have labels to improve flow readability. Labels appear in the visual flow designer and help document the flow logic.</p>");
                DESCRIPTIONS.put("MultipleTransitionsNoCondition",
                                "<p>When a task has multiple outgoing transitions, they should have conditions to control which path is taken. Without conditions, behavior may be non-deterministic.</p>");
                DESCRIPTIONS.put("DuplicateActivityName",
                                "<p>Activity names within a flow should be unique. Duplicate names cause confusion when reading logs, debugging, or referencing activity outputs.</p>");
                DESCRIPTIONS.put("FlowNamingConvention",
                                "<p>Flow names should follow snake_case convention (lowercase letters, digits, underscores). Consistent naming improves readability and maintainability.</p>");
                DESCRIPTIONS.put("RestActivityTimeout",
                                "<p>REST activities should have a timeout configured to prevent the flow from hanging indefinitely if the remote service is unresponsive.</p>");
                DESCRIPTIONS.put("NoRetryOnError",
                                "<p>Activities making external calls (REST, JDBC, etc.) should have retry configuration for resilience against transient failures.</p>");
                DESCRIPTIONS.put("EmptyActivityInput",
                                "<p>Activities (other than Start, Return, and Log) without input mappings may indicate incomplete configuration.</p>");
                DESCRIPTIONS.put("ComplexMappingExpression",
                                "<p>Very long or complex mapping expressions are hard to read and maintain. Consider using intermediate variables or subflows to simplify.</p>");
                DESCRIPTIONS.put("TriggerNoAuth",
                                "<p>REST triggers should have authentication configured (e.g., Basic, OAuth, JWT). Exposing endpoints without authentication is a critical security vulnerability.</p>");
                DESCRIPTIONS.put("TriggerInsecureHTTP",
                                "<p>REST triggers should use HTTPS (secureConnection=true) to encrypt data in transit. HTTP connections are vulnerable to eavesdropping and man-in-the-middle attacks.</p>");
                DESCRIPTIONS.put("TriggerNoDescription",
                                "<p>Trigger handlers should have descriptions explaining what endpoint they expose and what they do.</p>");
                DESCRIPTIONS.put("TriggerTooManyHandlers",
                                "<p>Triggers with too many handlers become hard to manage. Consider splitting into multiple triggers for better organization.</p>");
                DESCRIPTIONS.put("AppNoDescription",
                                "<p>The application should have a description explaining its purpose, key features, and any important configuration notes.</p>");
                DESCRIPTIONS.put("SecretInProperties",
                                "<p>Application properties that contain secrets (passwords, API keys, tokens) should use encrypted values (SECRET:...) or environment variable references, never plaintext.</p>");
                DESCRIPTIONS.put("UnusedProperty",
                                "<p>Properties that are defined but never referenced via $property[\"...\"] are dead configuration. They add clutter and may confuse developers.</p>");
                DESCRIPTIONS.put("UnusedConnection",
                                "<p>Connections that are defined but not referenced by any activity are unused. They should be removed to reduce clutter.</p>");
                DESCRIPTIONS.put("ConnectionNoTLS",
                                "<p>Connections to external services should use TLS/SSL for secure communication. Unencrypted connections expose data to interception.</p>");
                DESCRIPTIONS.put("ConnectionTimeout",
                                "<p>Connections should have a timeout configured to prevent indefinite waits when external services are unavailable.</p>");
                DESCRIPTIONS.put("DuplicateFlowName",
                                "<p>Flow names must be unique within an application. Duplicate names cause ambiguity in flow references and trigger handler bindings.</p>");
                DESCRIPTIONS.put("DefaultAppVersion",
                                "<p>Applications should have a meaningful version number reflecting their release state, not the default '1.0.0'.</p>");
                DESCRIPTIONS.put("TooManyProperties",
                                "<p>Applications with too many properties become hard to configure and maintain. Consider grouping related properties or using structured configuration.</p>");
                DESCRIPTIONS.put("PropertyNamingConvention",
                                "<p>Property names should follow consistent naming conventions: UPPER_SNAKE_CASE for simple properties or dotted.camelCase for grouped properties.</p>");
                DESCRIPTIONS.put("DisabledSSLVerification",
                                "<p>Disabling SSL/TLS certificate verification makes the application vulnerable to man-in-the-middle attacks. Only disable for development/testing, never in production.</p>");
                DESCRIPTIONS.put("InsecureConnection",
                                "<p>Connections using HTTP scheme instead of HTTPS transmit data in clear text. Use HTTPS for all external connections.</p>");
                DESCRIPTIONS.put("MissingImport",
                                "<p>Activity references used in flows should have corresponding entries in the imports array. Missing imports may cause runtime failures.</p>");
                DESCRIPTIONS.put("OrphanHandler",
                                "<p>Trigger handlers must reference flows that exist in the application. An orphan handler points to a non-existent flow, causing runtime failures when the endpoint is called.</p>");
                DESCRIPTIONS.put("DuplicateHandlerPath",
                                "<p>Each HTTP method + path combination on a trigger must be unique. Duplicate handler paths cause ambiguous routing and unpredictable behavior.</p>");
                DESCRIPTIONS.put("EmptyFlow",
                                "<p>A flow with no meaningful activities (only Start or empty) serves no purpose. Remove it or add activities to implement business logic.</p>");
                DESCRIPTIONS.put("FlowComplexity",
                                "<p>Flows with too many conditional branches are hard to understand, test, and maintain. Consider splitting into subflows or simplifying the branching logic.</p>");
                DESCRIPTIONS.put("SensitiveDataInLog",
                                "<p>Log activities must not output sensitive data such as passwords, tokens, API keys, or credentials. Sensitive data in logs can be exposed to unauthorized users through log aggregation systems.</p>");
                DESCRIPTIONS.put("FlowNoLogging",
                                "<p>Flows with multiple activities should include logging for observability. Without logging, troubleshooting production issues becomes extremely difficult.</p>");
                DESCRIPTIONS.put("ConnectionCredential",
                                "<p>Connection settings containing credentials (passwords, API keys, tokens) must use property references ($property[\"..\"]) or environment variables ($env[\"..\"]) instead of plaintext values. Plaintext credentials in application files are a critical security vulnerability.</p>");
                DESCRIPTIONS.put("InsecurePropertyURL",
                                "<p>Application properties containing URLs should use HTTPS, not HTTP. HTTP transmits data in clear text and is vulnerable to eavesdropping and man-in-the-middle attacks.</p>");
                DESCRIPTIONS.put("HardcodedPropertyURL",
                                "<p>Application properties containing hardcoded URLs make it difficult to deploy across environments (dev, staging, production). Use environment variable references ($env[\"..\"]) so URLs can be configured per environment without modifying the application.</p>");
                DESCRIPTIONS.put("SubflowNoTimeout",
                                "<p>Subflow activities should have an execution timeout (execTimeout) configured. Without a timeout, a hung subflow will block the parent flow indefinitely, consuming resources and potentially causing cascading failures.</p>");
                DESCRIPTIONS.put("SleepActivity",
                                "<p>Sleep activities block the goroutine and degrade application performance. In a Go-based runtime like Flogo, sleeping threads waste resources. Use Timer triggers, the Wait/Notify pattern, or retry-with-backoff configurations instead.</p>");
                DESCRIPTIONS.put("RestErrorSilenced",
                                "<p>REST activities with throwError=false and no status code checking silently ignore HTTP errors (4xx/5xx). This can lead to data corruption, missed failures, and difficult-to-diagnose production issues. Either enable throwError, configure response codes handling, or add conditional transitions checking statusCode.</p>");
                DESCRIPTIONS.put("GrpcNoTLS",
                                "<p>gRPC triggers and activities should use TLS to encrypt communication. Unencrypted gRPC traffic can be intercepted on the network. Enable enableTLS=true and provide proper certificates.</p>");
                DESCRIPTIONS.put("GraphQLIntrospection",
                                "<p>GraphQL introspection allows clients to query the full schema, exposing internal data models, fields, and relationships. Disable introspection (introspection=false) in production to reduce information leakage.</p>");
                DESCRIPTIONS.put("TriggerDefaultPort",
                                "<p>Triggers using default ports (REST=9999, GraphQL=7879) should be reconfigured for production. Use application properties ($property[\"..\"]) for port configuration to support deployment flexibility across environments.</p>");
                DESCRIPTIONS.put("SendMailNoTLS",
                                "<p>Send Mail activities with Connection Security set to NONE transmit emails (including credentials) in clear text. Use TLS or SSL to encrypt SMTP communication.</p>");
                DESCRIPTIONS.put("UnboundTriggerHandler",
                                "<p>Every trigger handler must be bound to a flow via its flowURI setting. A handler without a flow binding will fail at runtime when the trigger receives an event.</p>");
                DESCRIPTIONS.put("UnusedFlow",
                                "<p>Flows not referenced by any trigger handler or subflow activity are dead code. They add to application size and complexity without providing value. Remove unused flows or bind them to trigger handlers.</p>");
                DESCRIPTIONS.put("EmptyTransitionCondition",
                                "<p>A transition with type=expression but an empty condition value will never evaluate to true, making the downstream path unreachable. Provide a valid boolean expression (e.g. <code>$activity[REST].output.statusCode == 200</code>) or change the transition type to 'default'.</p>");
                DESCRIPTIONS.put("CircularLink",
                                "<p>Circular transitions (A → B → C → A) create infinite loops that will consume all resources at runtime. If iteration is intended, use the built-in Iterator/Repeat constructs which provide controlled looping with exit conditions.</p>");
                DESCRIPTIONS.put("BrokenResolverReference",
                                "<p>An input mapping references <code>$activity[TaskName]</code> but the referenced task ID does not exist in the current flow. This will fail at runtime with a resolver error. Verify the task ID spelling or update the mapping after renaming/removing activities.</p>");
                DESCRIPTIONS.put("DuplicateLink",
                                "<p>Multiple transitions with the same source, target, and condition are redundant. Only one will be evaluated. Remove duplicate links to clarify flow logic.</p>");
                DESCRIPTIONS.put("WebSocketNoTLS",
                                "<p>WebSocket triggers without TLS (WSS) transmit data in clear text. Enable secureConnection or enableTLS to encrypt WebSocket traffic and prevent eavesdropping.</p>");
                DESCRIPTIONS.put("CORSWildcard",
                                "<p>Using CORS with wildcard origin (<code>corsOrigins: *</code>) allows any website to make cross-origin requests to your API. This can lead to data theft via malicious sites. Restrict CORS origins to specific trusted domains.</p>");
                DESCRIPTIONS.put("TriggerHardcodedCredentials",
                                "<p>Trigger settings contain hardcoded credentials (passwords, API keys, tokens). These are visible in the .flogo JSON file and may be committed to source control. Use application properties with <code>$property[\"...\"]</code> references or encrypted <code>SECRET:</code> values instead.</p>");
                DESCRIPTIONS.put("ActivitySkipTLSVerify",
                                "<p>Activity settings that skip TLS/SSL certificate verification (<code>skipTlsVerify</code>, <code>skipSSLVerify</code>) disable certificate validation, making the connection vulnerable to man-in-the-middle attacks. Only use for local development with self-signed certificates, never in production.</p>");
                DESCRIPTIONS.put("DeprecatedFunction",
                                "<p>Mapping expressions use deprecated functions such as <code>string.tostring()</code> which may behave inconsistently. Migrate to the OOTB equivalent: <code>coerce.toString()</code>, <code>coerce.toInt()</code>, <code>coerce.toFloat64()</code>.</p>");
                DESCRIPTIONS.put("MD5HashFunction",
                                "<p>The <code>util.md5()</code> function uses the MD5 hash algorithm, which is cryptographically broken — collisions can be generated in seconds. Use <code>util.sha256()</code> for integrity checks, signatures, or any security-sensitive hashing.</p>");
                DESCRIPTIONS.put("HardcodedCryptoKey",
                                "<p>The <code>util.hmacSha256()</code> function is called with a hardcoded key literal in the mapping expression. HMAC keys must be kept secret — store them in application properties with <code>$property[\"...\"]</code> or environment variables with <code>$env[\"...\"]</code>.</p>");
                DESCRIPTIONS.put("RegexInjectionRisk",
                                "<p>The <code>string.regexExtract()</code> function is used with a pattern that references external input (<code>$flow</code>, <code>$trigger</code>). Malicious regex patterns with nested quantifiers can cause catastrophic backtracking (ReDoS). Use a hardcoded pattern or validate/sanitize the input before use.</p>");
                DESCRIPTIONS.put("SQLConcatInMapping",
                                "<p>A database activity input builds SQL queries using <code>string.concat()</code> with external input values. This is vulnerable to SQL injection. Use parameterized queries or prepared statements provided by the database activity instead of string concatenation.</p>");
        }
}
