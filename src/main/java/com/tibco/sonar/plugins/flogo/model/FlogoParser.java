package com.tibco.sonar.plugins.flogo.model;

import com.google.gson.*;
import java.util.*;

public class FlogoParser {

    private static final Gson GSON = new GsonBuilder().create();

    public static FlogoApp parse(String jsonContent) {
        FlogoApp app = GSON.fromJson(jsonContent, FlogoApp.class);
        app.setRawContent(jsonContent);

        // Convert resources into flows
        List<FlogoFlow> flows = new ArrayList<>();
        if (app.getResources() != null) {
            for (FlogoResource resource : app.getResources()) {
                if (resource.isFlow() && resource.getData() != null) {
                    FlogoFlow flow = parseFlow(resource);
                    flows.add(flow);
                }
            }
        }
        app.setFlows(flows);
        return app;
    }

    @SuppressWarnings("unchecked")
    private static FlogoFlow parseFlow(FlogoResource resource) {
        FlogoFlow flow = new FlogoFlow();
        flow.setId(resource.getId());
        Map<String, Object> data = resource.getData();

        flow.setName(getStringValue(data, "name"));
        flow.setDescription(getStringValue(data, "description"));
        flow.setExplicitReply(getBooleanValue(data, "explicitReply"));

        // Parse tasks
        Object tasksObj = data.get("tasks");
        if (tasksObj instanceof List) {
            List<Map<String, Object>> taskList = (List<Map<String, Object>>) tasksObj;
            for (Map<String, Object> taskMap : taskList) {
                FlogoTask task = parseTask(taskMap);
                flow.getTasks().add(task);
            }
        }

        // Parse links
        Object linksObj = data.get("links");
        if (linksObj instanceof List) {
            List<Map<String, Object>> linkList = (List<Map<String, Object>>) linksObj;
            for (Map<String, Object> linkMap : linkList) {
                FlogoLink link = parseLink(linkMap);
                flow.getLinks().add(link);
            }
        }

        Object metaObj = data.get("metadata");
        if (metaObj instanceof Map) {
            flow.setMetadata((Map<String, Object>) metaObj);
        }

        // Parse errorHandler block (nested flow with its own tasks/links)
        Object errorHandlerObj = data.get("errorHandler");
        if (errorHandlerObj instanceof Map) {
            Map<String, Object> ehMap = (Map<String, Object>) errorHandlerObj;
            flow.setHasErrorHandler(true);
            Object ehTasks = ehMap.get("tasks");
            if (ehTasks instanceof List) {
                List<Map<String, Object>> ehTaskList = (List<Map<String, Object>>) ehTasks;
                for (Map<String, Object> taskMap : ehTaskList) {
                    FlogoTask task = parseTask(taskMap);
                    flow.getErrorHandlerTasks().add(task);
                }
            }
        }

        return flow;
    }

    @SuppressWarnings("unchecked")
    private static FlogoTask parseTask(Map<String, Object> taskMap) {
        FlogoTask task = new FlogoTask();
        task.setId(getStringValue(taskMap, "id"));
        task.setName(getStringValue(taskMap, "name"));
        task.setDescription(getStringValue(taskMap, "description"));

        Object settingsObj = taskMap.get("settings");
        if (settingsObj instanceof Map) {
            task.setSettings((Map<String, Object>) settingsObj);
        }

        Object activityObj = taskMap.get("activity");
        if (activityObj instanceof Map) {
            FlogoActivity activity = parseActivity((Map<String, Object>) activityObj);
            task.setActivity(activity);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    private static FlogoActivity parseActivity(Map<String, Object> actMap) {
        FlogoActivity activity = new FlogoActivity();
        activity.setRef(getStringValue(actMap, "ref"));

        Object inputObj = actMap.get("input");
        if (inputObj instanceof Map) {
            activity.setInput((Map<String, Object>) inputObj);
        }
        Object outputObj = actMap.get("output");
        if (outputObj instanceof Map) {
            activity.setOutput((Map<String, Object>) outputObj);
        }
        Object settingsObj = actMap.get("settings");
        if (settingsObj instanceof Map) {
            activity.setSettings((Map<String, Object>) settingsObj);
        }
        Object schemasObj = actMap.get("schemas");
        if (schemasObj instanceof Map) {
            activity.setSchemas((Map<String, Object>) schemasObj);
        }
        return activity;
    }

    private static FlogoLink parseLink(Map<String, Object> linkMap) {
        FlogoLink link = new FlogoLink();
        Object idObj = linkMap.get("id");
        if (idObj instanceof Number) {
            link.setId(((Number) idObj).intValue());
        }
        link.setFrom(getStringValue(linkMap, "from"));
        link.setTo(getStringValue(linkMap, "to"));
        link.setType(getStringValue(linkMap, "type"));
        link.setLabel(getStringValue(linkMap, "label"));
        link.setValue(getStringValue(linkMap, "value"));
        return link;
    }

    private static String getStringValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private static boolean getBooleanValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Boolean)
            return (Boolean) val;
        if (val instanceof String)
            return Boolean.parseBoolean((String) val);
        return false;
    }

    // --- Line index cache: avoids repeated content.split("\n") calls ---
    private static String cachedContentRef;
    private static String[] cachedLines;

    private static String[] getLines(String content) {
        if (content != cachedContentRef) {
            cachedContentRef = content;
            cachedLines = content.split("\n");
        }
        return cachedLines;
    }

    /**
     * Find the 1-based line number in raw content where a search term first
     * appears. Returns 0 if not found.
     */
    public static int findLineNumber(String content, String searchTerm) {
        if (content == null || searchTerm == null)
            return 0;
        String[] lines = getLines(content);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(searchTerm)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Find line number for a named element (flow, task, trigger, etc.)
     * Returns 0 if not found.
     */
    public static int findElementLine(String content, String elementName) {
        // Try exact JSON field match first
        int line = findLineNumber(content, "\"name\": \"" + elementName + "\"");
        if (line > 0)
            return line;
        line = findLineNumber(content, "\"name\":\"" + elementName + "\"");
        if (line > 0)
            return line;
        // Try id field
        line = findLineNumber(content, "\"id\": \"" + elementName + "\"");
        if (line > 0)
            return line;
        return findLineNumber(content, "\"id\":\"" + elementName + "\"");
    }
}
