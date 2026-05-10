package com.tibco.sonar.plugins.flogo.model;

import java.util.*;

public class FlogoFlow {
    private String id;
    private String name;
    private String description;
    private List<FlogoTask> tasks = new ArrayList<>();
    private List<FlogoLink> links = new ArrayList<>();
    private boolean explicitReply;
    private boolean hasErrorHandler;
    private List<FlogoTask> errorHandlerTasks = new ArrayList<>();
    private Map<String, Object> metadata = new LinkedHashMap<>();

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

    public List<FlogoTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<FlogoTask> tasks) {
        this.tasks = tasks;
    }

    public List<FlogoLink> getLinks() {
        return links;
    }

    public void setLinks(List<FlogoLink> links) {
        this.links = links;
    }

    public boolean isExplicitReply() {
        return explicitReply;
    }

    public void setExplicitReply(boolean explicitReply) {
        this.explicitReply = explicitReply;
    }

    public boolean hasErrorHandler() {
        return hasErrorHandler;
    }

    public void setHasErrorHandler(boolean hasErrorHandler) {
        this.hasErrorHandler = hasErrorHandler;
    }

    public List<FlogoTask> getErrorHandlerTasks() {
        return errorHandlerTasks;
    }

    public void setErrorHandlerTasks(List<FlogoTask> errorHandlerTasks) {
        this.errorHandlerTasks = errorHandlerTasks;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public FlogoTask findTaskById(String taskId) {
        return tasks.stream().filter(t -> taskId.equals(t.getId())).findFirst().orElse(null);
    }

    public List<FlogoLink> getOutgoingLinks(String taskId) {
        return links.stream().filter(l -> taskId.equals(l.getFrom())).toList();
    }

    public List<FlogoLink> getIncomingLinks(String taskId) {
        return links.stream().filter(l -> taskId.equals(l.getTo())).toList();
    }

    public boolean hasTaskWithRef(String ref) {
        return tasks.stream().anyMatch(t -> t.getActivity() != null && ref.equals(t.getActivity().getRef()));
    }

    public Set<String> getReachableTaskIds() {
        Set<String> reachable = new HashSet<>();
        FlogoTask start = tasks.stream().filter(t -> "Start".equals(t.getId())).findFirst().orElse(null);
        if (start == null && !tasks.isEmpty()) {
            start = tasks.get(0);
        }
        if (start != null) {
            collectReachable(start.getId(), reachable);
        }
        return reachable;
    }

    private void collectReachable(String taskId, Set<String> visited) {
        if (visited.contains(taskId))
            return;
        visited.add(taskId);
        for (FlogoLink link : getOutgoingLinks(taskId)) {
            collectReachable(link.getTo(), visited);
        }
    }
}
