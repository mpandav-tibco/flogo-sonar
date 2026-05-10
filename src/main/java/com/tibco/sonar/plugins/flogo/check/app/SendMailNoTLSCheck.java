package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;

@Rule(key = "SendMailNoTLS", name = "Send Mail activity should use TLS or SSL", priority = Priority.CRITICAL, tags = {
        "security", "owasp-a02" })
public class SendMailNoTLSCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        for (FlogoFlow flow : app.getFlows()) {
            for (FlogoTask task : flow.getTasks()) {
                if (task.getActivity() == null || !task.getActivity().isSendMailRef())
                    continue;

                Object connSecurity = task.getActivity().getInputValue("Connection Security");
                if ("NONE".equalsIgnoreCase(String.valueOf(connSecurity))) {
                    addIssue("Send Mail activity '" + task.getId() + "' in flow '" + flow.getName()
                            + "' uses no connection security (NONE). Use TLS or SSL to encrypt email communication.",
                            FlogoParser.findElementLine(app.getRawContent(), task.getId()));
                }
            }
        }
    }
}
