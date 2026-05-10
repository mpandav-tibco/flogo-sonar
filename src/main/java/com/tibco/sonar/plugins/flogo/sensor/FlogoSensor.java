package com.tibco.sonar.plugins.flogo.sensor;

import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import com.tibco.sonar.plugins.flogo.language.FlogoLanguage;
import com.tibco.sonar.plugins.flogo.metric.FlogoMetrics;
import com.tibco.sonar.plugins.flogo.model.*;
import com.tibco.sonar.plugins.flogo.rulerepository.FlogoRuleDefinition;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.sonar.api.batch.sensor.coverage.NewCoverage;

public class FlogoSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(FlogoSensor.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(FlogoLanguage.KEY);
        descriptor.name("Flogo Sensor");
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        FilePredicate predicate = fs.predicates().hasLanguage(FlogoLanguage.KEY);

        for (InputFile inputFile : fs.inputFiles(predicate)) {
            LOG.info("Analyzing Flogo file: " + inputFile.filename());
            try {
                analyzeFile(context, inputFile);
            } catch (Exception e) {
                LOG.error("Error analyzing file: " + inputFile.filename(), e);
            }
        }
    }

    private void analyzeFile(SensorContext context, InputFile inputFile) throws IOException {
        String content = inputFile.contents();
        FlogoApp app = FlogoParser.parse(content);

        // Load test metadata into app model for test quality checks
        loadTestMetadata(inputFile, app);

        // Save metrics (including NCLOC and coverage)
        saveMetrics(context, inputFile, app, content);

        // Run all checks (pre-instantiated to avoid per-file reflection)
        List<AbstractFlogoCheck> checks = createChecks();
        for (AbstractFlogoCheck check : checks) {
            try {
                check.clearIssues();
                check.validate(app);

                String ruleKey = check.getRuleKey();
                for (AbstractFlogoCheck.FlogoIssue issue : check.getIssues()) {
                    saveIssue(context, inputFile, ruleKey, issue);
                }
            } catch (Exception e) {
                LOG.warn("Error running check " + check.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private static List<AbstractFlogoCheck> createChecks() {
        List<AbstractFlogoCheck> checks = new ArrayList<>();
        for (Class<? extends AbstractFlogoCheck> checkClass : FlogoRuleDefinition.getCheckClasses()) {
            try {
                checks.add(checkClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                LOG.warn("Could not instantiate check " + checkClass.getSimpleName() + ": " + e.getMessage());
            }
        }
        return checks;
    }

    private void saveIssue(SensorContext context, InputFile inputFile, String ruleKey,
            AbstractFlogoCheck.FlogoIssue issue) {
        RuleKey rule = RuleKey.of(FlogoRuleDefinition.REPOSITORY_KEY, ruleKey);
        NewIssue newIssue = context.newIssue().forRule(rule);

        int line = issue.getLine();
        int maxLines = inputFile.lines();
        if (line < 1)
            line = 1;
        if (line > maxLines)
            line = maxLines;

        NewIssueLocation location = newIssue.newLocation()
                .on(inputFile)
                .at(inputFile.selectLine(line))
                .message(issue.getMessage());

        newIssue.at(location).save();
    }

    /**
     * Load .flogotest metadata into the FlogoApp model so test quality checks
     * can inspect test cases, assertions, inputs, and suites.
     */
    @SuppressWarnings("unchecked")
    private void loadTestMetadata(InputFile inputFile, FlogoApp app) {
        try {
            String flogoPath = inputFile.uri().getPath();
            String testPath = flogoPath.replace(".flogo", ".flogotest");
            File testFile = new File(testPath);
            if (!testFile.exists())
                return;

            String testContent = new String(Files.readAllBytes(testFile.toPath()));
            JsonObject testJson = JsonParser.parseString(testContent).getAsJsonObject();

            // Parse test cases
            Map<String, Map<String, Object>> testCases = new LinkedHashMap<>();
            if (testJson.has("tests")) {
                JsonObject tests = testJson.getAsJsonObject("tests");
                for (Map.Entry<String, JsonElement> entry : tests.entrySet()) {
                    JsonObject tc = entry.getValue().getAsJsonObject();
                    Map<String, Object> tcMap = new LinkedHashMap<>();
                    tcMap.put("key", entry.getKey());
                    tcMap.put("name", tc.has("name") ? tc.get("name").getAsString() : entry.getKey());
                    tcMap.put("flowId", tc.has("flowId") ? tc.get("flowId").getAsString() : "");
                    tcMap.put("flowName", tc.has("flowName") ? tc.get("flowName").getAsString() : "");
                    tcMap.put("description", tc.has("description") ? tc.get("description").getAsString() : "");

                    // Count assertions
                    int assertionCount = 0;
                    if (tc.has("flowOutputs")) {
                        JsonObject outputs = tc.getAsJsonObject("flowOutputs");
                        if (outputs.has("assertions")) {
                            assertionCount = outputs.getAsJsonObject("assertions").size();
                        }
                    }
                    tcMap.put("assertionCount", assertionCount);

                    // Check input quality
                    boolean hasInputs = false;
                    if (tc.has("flowInputs")) {
                        hasInputs = hasNonEmptyValues(tc.getAsJsonObject("flowInputs"));
                    }
                    tcMap.put("hasInputs", hasInputs);

                    testCases.put(entry.getKey(), tcMap);
                }
            }
            app.setTestCases(testCases);

            // Parse suites
            Map<String, Map<String, Object>> suites = new LinkedHashMap<>();
            if (testJson.has("suites")) {
                JsonObject suitesJson = testJson.getAsJsonObject("suites");
                for (Map.Entry<String, JsonElement> entry : suitesJson.entrySet()) {
                    JsonObject suite = entry.getValue().getAsJsonObject();
                    Map<String, Object> suiteMap = new LinkedHashMap<>();
                    suiteMap.put("name", suite.has("name") ? suite.get("name").getAsString() : entry.getKey());
                    suiteMap.put("disabled", suite.has("disabled") && suite.get("disabled").getAsBoolean());

                    List<String> testRefs = new ArrayList<>();
                    if (suite.has("tests")) {
                        for (JsonElement ref : suite.getAsJsonArray("tests")) {
                            testRefs.add(ref.getAsString());
                        }
                    }
                    suiteMap.put("tests", testRefs);
                    suites.put(entry.getKey(), suiteMap);
                }
            }
            app.setTestSuites(suites);
        } catch (Exception e) {
            LOG.debug("Could not load test metadata for {}: {}", inputFile.filename(), e.getMessage());
        }
    }

    private void saveMetrics(SensorContext context, InputFile inputFile, FlogoApp app, String content) {
        // --- Core metrics: NCLOC ---
        String[] lines = content.split("\n");
        int ncloc = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                ncloc++;
            }
        }
        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.NCLOC)
                .on(inputFile)
                .withValue(ncloc)
                .save();

        // --- Coverage metrics ---
        saveCoverageMetrics(context, inputFile, app, content);

        // --- Custom Flogo metrics ---
        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_FLOWS)
                .on(inputFile)
                .withValue(app.getTotalFlowCount())
                .save();

        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_ACTIVITIES)
                .on(inputFile)
                .withValue(app.getTotalActivityCount())
                .save();

        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_TRIGGERS)
                .on(inputFile)
                .withValue(app.getTriggers().size())
                .save();

        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_CONNECTIONS)
                .on(inputFile)
                .withValue(app.getConnections() != null ? app.getConnections().size() : 0)
                .save();

        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_PROPERTIES)
                .on(inputFile)
                .withValue(app.getProperties().size())
                .save();

        context.<Integer>newMeasure()
                .forMetric(FlogoMetrics.FLOGO_HANDLERS)
                .on(inputFile)
                .withValue(app.getTotalTriggerHandlerCount())
                .save();
    }

    /**
     * Calculate and save coverage metrics based on Flogo test files (.flogotest).
     * Each task/activity in a flow is a coverable unit.
     * Tasks in flows covered by test cases in the .flogotest file are considered
     * covered.
     */
    @SuppressWarnings("unchecked")
    private void saveCoverageMetrics(SensorContext context, InputFile inputFile, FlogoApp app, String content) {
        // Collect all coverable lines (task definition lines)
        Map<String, Set<Integer>> flowTaskLines = new LinkedHashMap<>();
        for (FlogoFlow flow : app.getFlows()) {
            Set<Integer> taskLines = new LinkedHashSet<>();
            for (FlogoTask task : flow.getTasks()) {
                int line = FlogoParser.findElementLine(content, task.getId());
                if (line > 0) {
                    taskLines.add(line);
                }
            }
            // Also count error handler tasks
            for (FlogoTask task : flow.getErrorHandlerTasks()) {
                int line = FlogoParser.findElementLine(content, task.getId());
                if (line > 0) {
                    taskLines.add(line);
                }
            }
            if (!taskLines.isEmpty()) {
                flowTaskLines.put(flow.getId(), taskLines);
            }
        }

        // Also count trigger handler lines as coverable
        Set<Integer> triggerLines = new LinkedHashSet<>();
        for (FlogoTrigger trigger : app.getTriggers()) {
            int line = FlogoParser.findElementLine(content, trigger.getId());
            if (line > 0) {
                triggerLines.add(line);
            }
        }

        // Total coverable lines
        Set<Integer> allCoverableLines = new LinkedHashSet<>();
        for (Set<Integer> taskLines : flowTaskLines.values()) {
            allCoverableLines.addAll(taskLines);
        }
        allCoverableLines.addAll(triggerLines);

        if (allCoverableLines.isEmpty()) {
            return;
        }

        // Find tested flows from already-loaded test metadata (avoids re-parsing
        // .flogotest)
        Map<String, TestQuality> testedFlows = buildTestedFlowsFromMetadata(app);

        // Calculate covered lines — only full coverage for tests with assertions
        Set<Integer> coveredLines = new LinkedHashSet<>();
        for (Map.Entry<String, Set<Integer>> entry : flowTaskLines.entrySet()) {
            String flowId = entry.getKey();
            TestQuality quality = testedFlows.get(flowId);
            if (quality != null && quality.hasAssertions) {
                // Full coverage: test case with assertions
                coveredLines.addAll(entry.getValue());
            }
            // Test cases without assertions don't count as covered
        }

        // Report coverage using NewCoverage API
        NewCoverage coverage = context.newCoverage().onFile(inputFile);
        for (int line : allCoverableLines) {
            int hits = coveredLines.contains(line) ? 1 : 0;
            coverage.lineHits(line, hits);
        }
        coverage.save();

        int linesToCover = allCoverableLines.size();
        int uncoveredCount = linesToCover - coveredLines.size();
        LOG.info("Coverage for {}: {} lines to cover, {} uncovered, {} tested flows ({} with assertions)",
                inputFile.filename(), linesToCover, uncoveredCount,
                testedFlows.size(), testedFlows.values().stream().filter(q -> q.hasAssertions).count());
    }

    /** Quality metadata for a test case */
    private static class TestQuality {
        final String flowId;
        final String testName;
        final boolean hasAssertions;
        final boolean hasInputs;
        final boolean inSuite;

        TestQuality(String flowId, String testName, boolean hasAssertions, boolean hasInputs, boolean inSuite) {
            this.flowId = flowId;
            this.testName = testName;
            this.hasAssertions = hasAssertions;
            this.hasInputs = hasInputs;
            this.inSuite = inSuite;
        }
    }

    /**
     * Build tested flows map from the already-loaded test metadata in the app
     * model.
     * This avoids re-parsing the .flogotest file.
     */
    private Map<String, TestQuality> buildTestedFlowsFromMetadata(FlogoApp app) {
        Map<String, TestQuality> result = new LinkedHashMap<>();

        // Collect test case keys that are in a non-disabled suite
        Set<String> testsInSuites = new HashSet<>();
        for (Map<String, Object> suite : app.getTestSuites().values()) {
            boolean disabled = Boolean.TRUE.equals(suite.get("disabled"));
            if (!disabled) {
                Object tests = suite.get("tests");
                if (tests instanceof List) {
                    for (Object ref : (List<?>) tests) {
                        testsInSuites.add(ref.toString());
                    }
                }
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : app.getTestCases().entrySet()) {
            String testKey = entry.getKey();
            Map<String, Object> tc = entry.getValue();

            String flowId = (String) tc.getOrDefault("flowId", "");
            if (flowId.isEmpty())
                continue;

            String testName = (String) tc.getOrDefault("name", testKey);
            int assertionCount = (int) tc.getOrDefault("assertionCount", 0);
            boolean hasInputs = Boolean.TRUE.equals(tc.get("hasInputs"));
            boolean inSuite = testsInSuites.contains(testKey);

            TestQuality current = new TestQuality(flowId, testName, assertionCount > 0, hasInputs, inSuite);
            TestQuality existing = result.get(flowId);
            if (existing == null || betterQuality(current, existing)) {
                result.put(flowId, current);
            }
        }

        return result;
    }

    /** Check if a JSON object has any non-empty, non-null, non-trivial values */
    private boolean hasNonEmptyValues(JsonObject obj) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            JsonElement val = entry.getValue();
            if (val.isJsonNull())
                continue;
            if (val.isJsonPrimitive() && val.getAsString().isEmpty())
                continue;
            if (val.isJsonObject() && val.getAsJsonObject().size() == 0)
                continue;
            if (val.isJsonArray() && val.getAsJsonArray().size() == 0)
                continue;
            return true;
        }
        return false;
    }

    /** Compare test quality — assertions > inputs > suite membership */
    private boolean betterQuality(TestQuality a, TestQuality b) {
        if (a.hasAssertions != b.hasAssertions)
            return a.hasAssertions;
        if (a.hasInputs != b.hasInputs)
            return a.hasInputs;
        return a.inSuite && !b.inSuite;
    }
}
