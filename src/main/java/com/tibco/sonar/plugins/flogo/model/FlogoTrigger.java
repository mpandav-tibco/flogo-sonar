package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoTrigger {
    private String ref;
    private String name;
    private String description;
    private String id;
    private Map<String, Object> settings = new LinkedHashMap<>();
    private List<FlogoHandler> handlers = new ArrayList<>();

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public List<FlogoHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<FlogoHandler> handlers) {
        this.handlers = handlers;
    }

    public Object getSetting(String key) {
        return settings.get(key);
    }

    public String getSettingAsString(String key) {
        Object val = settings.get(key);
        return val != null ? val.toString() : null;
    }

    public boolean isRestTrigger() {
        return ref != null && ref.toLowerCase().contains("rest");
    }

    public boolean isGrpcTrigger() {
        return ref != null && ref.toLowerCase().contains("grpc");
    }

    public boolean isGraphQLTrigger() {
        return ref != null && ref.toLowerCase().contains("graphql");
    }

    public boolean isTimerTrigger() {
        return ref != null && ref.toLowerCase().contains("timer");
    }

    public boolean isLambdaTrigger() {
        return ref != null && ref.toLowerCase().contains("lambda");
    }
}
