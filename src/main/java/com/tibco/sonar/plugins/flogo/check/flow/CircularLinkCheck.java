package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.*;

@Rule(key = "CircularLink", name = "Flow contains circular transition path", priority = Priority.CRITICAL, tags = {
        "bug", "reliability" })
public class CircularLinkCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        // Build adjacency list
        Map<String, List<String>> adj = new HashMap<>();
        for (FlogoLink link : flow.getLinks()) {
            adj.computeIfAbsent(link.getFrom(), k -> new ArrayList<>()).add(link.getTo());
        }

        // DFS cycle detection
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        List<String> cyclePath = new ArrayList<>();

        for (FlogoTask task : flow.getTasks()) {
            if (!visited.contains(task.getId())) {
                if (hasCycle(task.getId(), adj, visited, inStack, cyclePath)) {
                    Collections.reverse(cyclePath);
                    String cycle = String.join(" -> ", cyclePath);
                    addIssue("Flow '" + flow.getName()
                            + "' contains a circular transition path: " + cycle
                            + ". This may cause infinite loops at runtime. "
                            + "Use an iteration/loop construct or add a termination condition.",
                            flowLine(app, flow));
                    return; // Report once per flow
                }
            }
        }
    }

    private boolean hasCycle(String node, Map<String, List<String>> adj,
            Set<String> visited, Set<String> inStack, List<String> cyclePath) {
        visited.add(node);
        inStack.add(node);

        List<String> neighbors = adj.getOrDefault(node, Collections.emptyList());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (hasCycle(neighbor, adj, visited, inStack, cyclePath)) {
                    cyclePath.add(node);
                    return true;
                }
            } else if (inStack.contains(neighbor)) {
                cyclePath.add(neighbor);
                cyclePath.add(node);
                return true;
            }
        }

        inStack.remove(node);
        return false;
    }
}
