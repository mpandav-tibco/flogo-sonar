package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoTask {
    private String id;
    private String name;
    private String description;
    private FlogoActivity activity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FlogoActivity getActivity() {
        return activity;
    }

    public void setActivity(FlogoActivity activity) {
        this.activity = activity;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public boolean isStartTask() {
        return "Start".equals(id) || (activity != null && "#noop".equals(activity.getRef()));
    }

    public boolean isReturnTask() {
        return activity != null && activity.getRef() != null && activity.getRef().contains("actreturn");
    }

    public boolean isRestActivity() {
        return activity != null && activity.getRef() != null && activity.getRef().toLowerCase().contains("rest");
    }

    public boolean isLogActivity() {
        return activity != null && activity.getRef() != null && activity.getRef().toLowerCase().contains("log");
    }

    public boolean hasRetryConfig() {
        if (settings == null || !settings.containsKey("retryOnError"))
            return false;
        Object retry = settings.get("retryOnError");
        if (retry instanceof Map) {
            Object count = ((Map<?, ?>) retry).get("count");
            if (count instanceof Number) {
                return ((Number) count).intValue() > 0;
            }
        }
        return true;
    }
}
