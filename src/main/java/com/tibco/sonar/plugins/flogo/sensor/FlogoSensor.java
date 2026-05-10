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

        // Save metrics (including NCLOC and coverage)
        saveMetrics(context, inputFile, app, content);

        // Run all checks
        for (Class<? extends AbstractFlogoCheck> checkClass : FlogoRuleDefinition.getCheckClasses()) {
            try {
                AbstractFlogoCheck check = checkClass.getDeclaredConstructor().newInstance();
                check.validate(app);

                String ruleKey = check.getRuleKey();
                for (AbstractFlogoCheck.FlogoIssue issue : check.getIssues()) {
                    saveIssue(context, inputFile, ruleKey, issue);
                }
            } catch (Exception e) {
                LOG.warn("Error running check " + checkClass.getSimpleName() + ": " + e.getMessage());
            }
        }
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
                .withValue(app.getConnections().size())
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

        // Find tested flows from .flogotest file
        Set<String> testedFlowIds = findTestedFlows(inputFile);

        // Calculate covered lines
        Set<Integer> coveredLines = new LinkedHashSet<>();
        for (Map.Entry<String, Set<Integer>> entry : flowTaskLines.entrySet()) {
            String flowId = entry.getKey();
            if (testedFlowIds.contains(flowId)) {
                coveredLines.addAll(entry.getValue());
            }
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
        LOG.info("Coverage for {}: {} lines to cover, {} uncovered, {} tested flows",
                inputFile.filename(), linesToCover, uncoveredCount, testedFlowIds.size());
    }

    /**
     * Parse the .flogotest file to find which flows have test cases.
     * Returns a set of flow IDs (e.g., "flow:my_flow") that are covered by tests.
     */
    @SuppressWarnings("unchecked")
    private Set<String> findTestedFlows(InputFile inputFile) {
        Set<String> testedFlowIds = new HashSet<>();
        try {
            // Look for .flogotest file alongside the .flogo file
            String flogoPath = inputFile.uri().getPath();
            String testPath = flogoPath.replace(".flogo", ".flogotest");
            File testFile = new File(testPath);

            if (!testFile.exists()) {
                LOG.debug("No test file found at: {}", testPath);
                return testedFlowIds;
            }

            String testContent = new String(Files.readAllBytes(testFile.toPath()));
            JsonObject testJson = JsonParser.parseString(testContent).getAsJsonObject();

            // Parse "tests" section: keys are "flowName:testCaseName", each has "flowId"
            if (testJson.has("tests")) {
                JsonObject tests = testJson.getAsJsonObject("tests");
                for (Map.Entry<String, JsonElement> entry : tests.entrySet()) {
                    JsonObject testCase = entry.getValue().getAsJsonObject();
                    if (testCase.has("flowId")) {
                        testedFlowIds.add(testCase.get("flowId").getAsString());
                    }
                }
            }

            LOG.info("Found {} tested flows in {}", testedFlowIds.size(), testFile.getName());
        } catch (Exception e) {
            LOG.warn("Error reading test file for {}: {}", inputFile.filename(), e.getMessage());
        }
        return testedFlowIds;
    }
}
