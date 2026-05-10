package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.*;

@Rule(key = "DuplicateLink", name = "Duplicate transition between same activities", priority = Priority.MAJOR, tags = {
        "bug", "maintainability" })
public class DuplicateLinkCheck extends AbstractFlowCheck {
    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        Map<String, Integer> linkCounts = new HashMap<>();
        for (FlogoLink link : flow.getLinks()) {
            // Key = from->to + condition type + value
            String key = link.getFrom() + "->" + link.getTo()
                    + "|" + (link.getType() != null ? link.getType() : "default")
                    + "|" + (link.getValue() != null ? link.getValue() : "");
            linkCounts.merge(key, 1, Integer::sum);
        }

        Set<String> reported = new HashSet<>();
        for (FlogoLink link : flow.getLinks()) {
            String key = link.getFrom() + "->" + link.getTo()
                    + "|" + (link.getType() != null ? link.getType() : "default")
                    + "|" + (link.getValue() != null ? link.getValue() : "");
            if (linkCounts.getOrDefault(key, 0) > 1 && reported.add(key)) {
                addIssue("Flow '" + flow.getName() + "' has duplicate transitions from '"
                        + link.getFrom() + "' to '" + link.getTo()
                        + "' with the same condition. Remove the duplicate link.",
                        taskLine(app, link.getFrom()));
            }
        }
    }
}
