package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoHandler {
    private String name;
    private String description;
    private Map<String, Object> settings = new LinkedHashMap<>();
    private Map<String, Object> action = new LinkedHashMap<>();
    private Map<String, Object> reply = new LinkedHashMap<>();
    private Map<String, Object> schemas = new LinkedHashMap<>();

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

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Map<String, Object> getAction() {
        return action;
    }

    public void setAction(Map<String, Object> action) {
        this.action = action;
    }

    public Map<String, Object> getReply() {
        return reply;
    }

    public void setReply(Map<String, Object> reply) {
        this.reply = reply;
    }

    public Map<String, Object> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Object> schemas) {
        this.schemas = schemas;
    }

    public String getSettingAsString(String key) {
        Object val = settings.get(key);
        return val != null ? val.toString() : null;
    }

    public String getFlowURI() {
        if (action != null) {
            Object settingsObj = action.get("settings");
            if (settingsObj instanceof Map) {
                Object uri = ((Map<?, ?>) settingsObj).get("flowURI");
                return uri != null ? uri.toString() : null;
            }
        }
        return null;
    }

    public String getMethod() {
        return getSettingAsString("Method");
    }

    public String getPath() {
        return getSettingAsString("Path");
    }
}
