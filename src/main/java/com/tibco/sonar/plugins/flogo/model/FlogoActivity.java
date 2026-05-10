package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoActivity {
    private String ref;
    private Map<String, Object> input = new LinkedHashMap<>();
    private Map<String, Object> output = new LinkedHashMap<>();
    private Map<String, Object> settings = new LinkedHashMap<>();
    private Map<String, Object> schemas = new LinkedHashMap<>();

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Map<String, Object> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Object> schemas) {
        this.schemas = schemas;
    }

    public Object getInputValue(String key) {
        return input != null ? input.get(key) : null;
    }

    public Object getOutputValue(String key) {
        return output != null ? output.get(key) : null;
    }

    public Object getSettingValue(String key) {
        return settings != null ? settings.get(key) : null;
    }

    public String getSettingAsString(String key) {
        Object val = getSettingValue(key);
        return val != null ? val.toString() : null;
    }

    public boolean isRestRef() {
        return ref != null && ref.toLowerCase().contains("rest");
    }

    public boolean isLogRef() {
        return ref != null && ref.toLowerCase().contains("log");
    }

    public boolean isReturnRef() {
        return ref != null && ref.contains("actreturn");
    }

    public boolean isNoopRef() {
        return "#noop".equals(ref);
    }

    public boolean isSubflowRef() {
        return ref != null && ref.contains("subflow");
    }

    public boolean isSleepRef() {
        return ref != null && ref.toLowerCase().contains("sleep");
    }

    public boolean isSendMailRef() {
        return ref != null && ref.toLowerCase().contains("sendmail");
    }

}
