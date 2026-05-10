package com.tibco.sonar.plugins.flogo.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FlogoParserTest {

    private String testContent;
    private FlogoApp app;

    @Before
    public void setUp() throws Exception {
        InputStream is = getClass().getResourceAsStream("/test-app.flogo");
        assertNotNull("Test fixture not found", is);
        testContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        app = FlogoParser.parse(testContent);
    }

    @Test
    public void testParseAppMetadata() {
        assertEquals("TestApp", app.getName());
        assertEquals("1.0.0", app.getVersion());
        assertEquals("flogo:app", app.getType());
    }

    @Test
    public void testParseImports() {
        assertEquals(3, app.getImports().size());
        assertTrue(app.getImports().get(0).contains("rest"));
    }

    @Test
    public void testParseFlows() {
        assertEquals(2, app.getFlows().size());
        assertEquals("get_orders", app.getFlows().get(0).getName());
        assertEquals("CreateOrder", app.getFlows().get(1).getName());
    }

    @Test
    public void testParseFlowTasks() {
        FlogoFlow flow = app.getFlows().get(0);
        assertEquals(4, flow.getTasks().size());
        assertEquals("Start", flow.getTasks().get(0).getId());
        assertEquals("REST_1", flow.getTasks().get(1).getId());
        assertEquals("Return", flow.getTasks().get(3).getId());
    }

    @Test
    public void testParseFlowLinks() {
        FlogoFlow flow = app.getFlows().get(0);
        assertEquals(3, flow.getLinks().size());
        assertEquals("Start", flow.getLinks().get(0).getFrom());
        assertEquals("REST_1", flow.getLinks().get(0).getTo());
    }

    @Test
    public void testParseActivity() {
        FlogoTask task = app.getFlows().get(0).getTasks().get(1); // REST_1
        assertNotNull(task.getActivity());
        assertEquals("#rest", task.getActivity().getRef());
        assertTrue(task.getActivity().isRestRef());
        assertEquals("GET", task.getActivity().getInputValue("method"));
    }

    @Test
    public void testParseTriggers() {
        assertEquals(1, app.getTriggers().size());
        FlogoTrigger trigger = app.getTriggers().get(0);
        assertEquals("MyRestTrigger", trigger.getName());
        assertEquals(2, trigger.getHandlers().size());
    }

    @Test
    public void testParseProperties() {
        assertEquals(2, app.getProperties().size());
        assertEquals("DB_HOST", app.getProperties().get(0).getName());
    }

    @Test
    public void testParseFlowDescription() {
        assertEquals("Fetches orders from the database", app.getFlows().get(0).getDescription());
        assertNull(app.getFlows().get(1).getDescription());
    }

    @Test
    public void testRawContentSet() {
        assertNotNull(app.getRawContent());
        assertEquals(testContent, app.getRawContent());
    }

    @Test
    public void testFindLineNumber() {
        int line = FlogoParser.findLineNumber(testContent, "\"name\": \"TestApp\"");
        assertTrue("Line should be > 0", line > 0);
    }

    @Test
    public void testFindLineNumberNotFound() {
        int line = FlogoParser.findLineNumber(testContent, "DOES_NOT_EXIST_XYZ");
        assertEquals("Not-found should return 0", 0, line);
    }

    @Test
    public void testFindElementLine() {
        int line = FlogoParser.findElementLine(testContent, "get_orders");
        assertTrue("Element line should be > 0", line > 0);
    }

    @Test
    public void testFindElementLineNotFound() {
        int line = FlogoParser.findElementLine(testContent, "nonexistent_flow");
        assertEquals("Not-found element should return 0", 0, line);
    }

    @Test
    public void testTaskHelperMethods() {
        FlogoTask start = app.getFlows().get(0).getTasks().get(0);
        assertTrue(start.isStartTask());
        assertFalse(start.isReturnTask());

        FlogoTask ret = app.getFlows().get(0).getTasks().get(3);
        assertTrue(ret.isReturnTask());
    }

    @Test
    public void testFlowReachableTasks() {
        FlogoFlow flow = app.getFlows().get(0);
        java.util.Set<String> reachable = flow.getReachableTaskIds();
        assertTrue(reachable.contains("Start"));
        assertTrue(reachable.contains("REST_1"));
        assertTrue(reachable.contains("Log_1"));
        assertTrue(reachable.contains("Return"));
    }

    @Test
    public void testTotalCounts() {
        assertEquals(2, app.getTotalFlowCount());
        assertEquals(6, app.getTotalActivityCount()); // 4 + 2
        assertEquals(2, app.getTotalTriggerHandlerCount());
    }

    @Test
    public void testHandlerFlowURI() {
        FlogoHandler handler = app.getTriggers().get(0).getHandlers().get(0);
        assertEquals("res://flow:get_orders", handler.getFlowURI());
    }

    @Test
    public void testLinkConditional() {
        FlogoLink link = app.getFlows().get(0).getLinks().get(0);
        assertEquals("default", link.getType());
        assertFalse(link.isConditional());
    }
}
