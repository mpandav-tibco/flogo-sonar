package com.tibco.sonar.plugins.flogo.model;

import com.google.gson.annotations.SerializedName;
import java.util.*;

public class FlogoApp {
    private String name;
    private String description;
    private String version;
    private String type;
    private String appModel;
    private List<String> imports = new ArrayList<>();
    private List<FlogoTrigger> triggers = new ArrayList<>();
    private List<FlogoResource> resources = new ArrayList<>();
    private List<FlogoProperty> properties = new ArrayList<>();
    private Map<String, FlogoConnection> connections = new LinkedHashMap<>();
    private Map<String, Object> schemas = new LinkedHashMap<>();

    // Transient fields populated after parsing
    private transient List<FlogoFlow> flows = new ArrayList<>();
    private transient String rawContent;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppModel() {
        return appModel;
    }

    public void setAppModel(String appModel) {
        this.appModel = appModel;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<FlogoTrigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<FlogoTrigger> triggers) {
        this.triggers = triggers;
    }

    public List<FlogoResource> getResources() {
        return resources;
    }

    public void setResources(List<FlogoResource> resources) {
        this.resources = resources;
    }

    public List<FlogoProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<FlogoProperty> properties) {
        this.properties = properties;
    }

    public Map<String, FlogoConnection> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, FlogoConnection> connections) {
        this.connections = connections;
    }

    public Map<String, Object> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Object> schemas) {
        this.schemas = schemas;
    }

    public List<FlogoFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<FlogoFlow> flows) {
        this.flows = flows;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public int getTotalActivityCount() {
        return flows.stream().mapToInt(f -> f.getTasks().size()).sum();
    }

    public int getTotalFlowCount() {
        return flows.size();
    }

    public int getTotalTriggerHandlerCount() {
        return triggers.stream().mapToInt(t -> t.getHandlers().size()).sum();
    }
}
