package com.tibco.sonar.plugins.flogo.metric;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import java.util.*;

public class FlogoMetrics implements Metrics {

    public static final Metric<Integer> FLOGO_FLOWS = new Metric.Builder(
            "flogo_flows", "Flogo Flows", Metric.ValueType.INT)
            .setDescription("Number of flows in the Flogo application")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    public static final Metric<Integer> FLOGO_ACTIVITIES = new Metric.Builder(
            "flogo_activities", "Flogo Activities", Metric.ValueType.INT)
            .setDescription("Total number of activities across all flows")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    public static final Metric<Integer> FLOGO_TRIGGERS = new Metric.Builder(
            "flogo_triggers", "Flogo Triggers", Metric.ValueType.INT)
            .setDescription("Number of triggers")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    public static final Metric<Integer> FLOGO_CONNECTIONS = new Metric.Builder(
            "flogo_connections", "Flogo Connections", Metric.ValueType.INT)
            .setDescription("Number of connections/connectors")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    public static final Metric<Integer> FLOGO_PROPERTIES = new Metric.Builder(
            "flogo_properties", "Flogo Properties", Metric.ValueType.INT)
            .setDescription("Number of application properties")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    public static final Metric<Integer> FLOGO_HANDLERS = new Metric.Builder(
            "flogo_handlers", "Flogo Handlers", Metric.ValueType.INT)
            .setDescription("Total number of trigger handlers (endpoints)")
            .setDirection(Metric.DIRECTION_NONE)
            .setQualitative(false)
            .setDomain("Flogo")
            .create();

    @Override
    public List<Metric> getMetrics() {
        return Arrays.asList(
                FLOGO_FLOWS, FLOGO_ACTIVITIES, FLOGO_TRIGGERS,
                FLOGO_CONNECTIONS, FLOGO_PROPERTIES, FLOGO_HANDLERS);
    }
}
