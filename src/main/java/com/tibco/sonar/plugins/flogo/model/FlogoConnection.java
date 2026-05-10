package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoConnection {
    private String id;
    private String name;
    private String ref;
    private boolean isGlobal;
    private Map<String, Object> settings = new LinkedHashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Object getSetting(String key) {
        return settings != null ? settings.get(key) : null;
    }

    public String getSettingAsString(String key) {
        Object val = getSetting(key);
        return val != null ? val.toString() : null;
    }

    public boolean getSettingAsBoolean(String key) {
        Object val = getSetting(key);
        if (val instanceof Boolean)
            return (Boolean) val;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        return false;
    }
}
