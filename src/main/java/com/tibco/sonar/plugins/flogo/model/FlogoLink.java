package com.tibco.sonar.plugins.flogo.model;

public class FlogoLink {
    private int id;
    private String from;
    private String to;
    private String type;
    private String label;
    private String value; // condition expression

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isConditional() {
        return "expression".equalsIgnoreCase(type)
                || "exprOtherwise".equalsIgnoreCase(type)
                || (value != null && !value.isEmpty());
    }

    public boolean hasLabel() {
        return label != null && !label.trim().isEmpty();
    }
}
