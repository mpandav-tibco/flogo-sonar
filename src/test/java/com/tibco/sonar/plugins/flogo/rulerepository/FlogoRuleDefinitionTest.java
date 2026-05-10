package com.tibco.sonar.plugins.flogo.rulerepository;

import com.tibco.sonar.plugins.flogo.check.AbstractFlogoCheck;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

public class FlogoRuleDefinitionTest {

    @Test
    public void testAllCheckClassesRegistered() {
        List<Class<? extends AbstractFlogoCheck>> classes = FlogoRuleDefinition.getCheckClasses();
        assertEquals("Expected 68 check classes", 68, classes.size());
    }

    @Test
    public void testAllCheckClassesInstantiable() {
        for (Class<? extends AbstractFlogoCheck> cls : FlogoRuleDefinition.getCheckClasses()) {
            try {
                AbstractFlogoCheck check = cls.getDeclaredConstructor().newInstance();
                assertNotNull("Check should instantiate: " + cls.getSimpleName(), check);
                assertNotNull("Check should have rule key: " + cls.getSimpleName(), check.getRuleKey());
            } catch (Exception e) {
                fail("Failed to instantiate " + cls.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    @Test
    public void testAllDescriptionResourcesExist() {
        for (Class<? extends AbstractFlogoCheck> cls : FlogoRuleDefinition.getCheckClasses()) {
            try {
                AbstractFlogoCheck check = cls.getDeclaredConstructor().newInstance();
                String ruleKey = check.getRuleKey();
                String path = "/rules/" + ruleKey + ".html";
                InputStream is = getClass().getResourceAsStream(path);
                assertNotNull("Missing description resource for rule: " + ruleKey, is);
            } catch (Exception e) {
                fail("Error checking " + cls.getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    @Test
    public void testNoDuplicateRuleKeys() {
        java.util.Set<String> keys = new java.util.HashSet<>();
        for (Class<? extends AbstractFlogoCheck> cls : FlogoRuleDefinition.getCheckClasses()) {
            try {
                AbstractFlogoCheck check = cls.getDeclaredConstructor().newInstance();
                String key = check.getRuleKey();
                assertTrue("Duplicate rule key: " + key, keys.add(key));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
}
