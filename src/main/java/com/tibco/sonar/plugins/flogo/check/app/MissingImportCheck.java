package com.tibco.sonar.plugins.flogo.check.app;

import com.tibco.sonar.plugins.flogo.check.AbstractAppCheck;
import com.tibco.sonar.plugins.flogo.model.*;
import org.sonar.check.Rule;
import org.sonar.check.Priority;
import java.util.*;

@Rule(key = "MissingImport", name = "Activity references should have matching imports", priority = Priority.CRITICAL, tags = {
        "bug" })
public class MissingImportCheck extends AbstractAppCheck {
    @Override
    protected void validateApp(FlogoApp app) {
        List<String> imports = app.getImports();
        // Build set of import aliases (first word before space)
        Set<String> importAliases = new HashSet<>();
        for (String imp : imports) {
            String trimmed = imp.trim();
            int spaceIdx = trimmed.indexOf(' ');
            if (spaceIdx > 0) {
                importAliases.add(trimmed.substring(0, spaceIdx));
            }
        }

        for (FlogoFlow flow : app.getFlows()) {
            for (FlogoTask task : flow.getTasks()) {
                if (task.getActivity() == null)
                    continue;
                String ref = task.getActivity().getRef();
                if (ref == null)
                    continue;

                if (ref.startsWith("#")) {
                    // Check alias ref against import aliases
                    String alias = ref.substring(1);
                    if (!importAliases.contains(alias)) {
                        addIssue(
                                "Activity ref '" + ref + "' used in task '" + task.getId() + "' of flow '"
                                        + flow.getName() + "' has no matching import alias.",
                                FlogoParser.findElementLine(app.getRawContent(), task.getId()));
                    }
                } else {
                    // Full ref — check against import paths
                    boolean found = imports.stream()
                            .anyMatch(imp -> imp.contains(ref) || ref.contains(imp));
                    if (!found) {
                        addIssue(
                                "Activity ref '" + ref + "' used in task '" + task.getId() + "' of flow '"
                                        + flow.getName() + "' has no matching import.",
                                FlogoParser.findElementLine(app.getRawContent(), task.getId()));
                    }
                }
            }
        }
    }
}
