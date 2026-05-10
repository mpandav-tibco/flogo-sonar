package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoResource {
    private String id;
    private Map<String, Object> data = new LinkedHashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public boolean isFlow() {
        return id != null && id.startsWith("flow:");
    }

    public String getFlowName() {
        if (isFlow()) {
            return id.substring("flow:".length());
        }
        return id;
    }
}
