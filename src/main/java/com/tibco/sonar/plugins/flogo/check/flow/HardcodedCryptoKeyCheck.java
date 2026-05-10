package com.tibco.sonar.plugins.flogo.check.flow;

import com.tibco.sonar.plugins.flogo.check.AbstractFlowCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

import java.util.Map;
import java.util.regex.*;

@Rule(key = "HardcodedCryptoKey", name = "HMAC/crypto function uses hardcoded key in mapping", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a02" })
public class HardcodedCryptoKeyCheck extends AbstractFlowCheck {

    // Matches util.hmacSha256(..., "literal") or util.hmacSha256(..., 'literal')
    // The key is the second argument — if it's a quoted string literal, it's
    // hardcoded
    private static final Pattern HMAC_HARDCODED = Pattern.compile(
            "util\\.hmacSha256\\([^,]+,\\s*[\"'][^\"']+[\"']\\)");

    @Override
    protected void validateFlow(FlogoFlow flow, FlogoApp app) {
        for (FlogoTask task : flow.getTasks()) {
            if (task.getActivity() == null)
                continue;
            checkMap(task.getActivity().getInput(), task, flow, app);
            checkMap(task.getActivity().getSettings(), task, flow, app);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkMap(Map<String, Object> map, FlogoTask task, FlogoFlow flow, FlogoApp app) {
        if (map == null)
            return;
        for (var entry : map.entrySet()) {
            if (entry.getValue() == null)
                continue;
            if (entry.getValue() instanceof Map) {
                checkMap((Map<String, Object>) entry.getValue(), task, flow, app);
                continue;
            }
            String val = entry.getValue().toString();
            if (!val.startsWith("="))
                continue;
            if (HMAC_HARDCODED.matcher(val).find()) {
                addIssue("Task '" + task.getId() + "' in flow '" + flow.getName()
                        + "' uses util.hmacSha256() with a hardcoded key in field '" + entry.getKey()
                        + "'. Use $property[\"...\"] or $env[\"...\"] to reference the key securely.",
                        taskLine(app, task.getId()));
            }
        }
    }
}
