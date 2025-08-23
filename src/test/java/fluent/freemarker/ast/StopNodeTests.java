package fluent.freemarker.ast;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StopNodeTests {

    @Test
    public void testStopNodeCreation() {
        FtlExpr expr = new LiteralExpr("error message");
        StopNode stopNode = new StopNode(expr);
        
        assertNotNull(stopNode);
        assertNotNull(stopNode.getExpr());
        assertEquals("error message", ((LiteralExpr)stopNode.getExpr()).getValue());
    }

    @Test
    public void testStopNodeSerialization() throws Exception {
        FtlExpr expr = new LiteralExpr("test stop");
        StopNode stopNode = new StopNode(expr);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(stopNode));
        assertNotNull(json);
        assertTrue(json.contains("test stop"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof StopNode);
        
        StopNode deserializedStop = (StopNode) deserializedNodes.get(0);
        assertNotNull(deserializedStop.getExpr());
    }

    @Test
    public void testStopNodeRendering() {
        FtlExpr expr = new LiteralExpr("Processing stopped");
        StopNode stopNode = new StopNode(expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(stopNode));
        assertTrue(rendered.contains("<#stop"));
        assertTrue(rendered.contains("Processing stopped"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testStopNodeToString() {
        FtlExpr expr = new LiteralExpr("stop message");
        StopNode stopNode = new StopNode(expr);
        
        String toString = stopNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Stop{"));
        assertTrue(toString.contains("stop message"));
    }

    @Test
    public void testStopNodeVisitorPattern() {
        FtlExpr expr = new LiteralExpr("visitor test");
        StopNode stopNode = new StopNode(expr);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(StopNode node) {
                assertNotNull(node);
                assertNotNull(node.getExpr());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> stopNode.accept(visitor));
    }

    @Test
    public void testStopNodeInTemplate() {
        FtlExpr expr = new LiteralExpr("Execution halted");
        StopNode stopNode = new StopNode(expr);
        
        // Test stop node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before stop"),
            stopNode,
            new TextNode("After stop")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before stop"));
        assertTrue(rendered.contains("Execution halted"));
        assertTrue(rendered.contains("After stop"));
    }

    @Test
    public void testStopNodeWithNullExpression() {
        StopNode stopNode = new StopNode(null);
        
        assertNotNull(stopNode);
        assertNull(stopNode.getExpr());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(stopNode));
        assertTrue(rendered.contains("<#stop"));
    }

    @Test
    public void testStopNodeWithComplexExpression() {
        FtlExpr expr = new LiteralExpr("Error in processing user " + "${username}");
        StopNode stopNode = new StopNode(expr);
        
        assertNotNull(stopNode.getExpr());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(stopNode));
        assertTrue(rendered.contains("Error in processing user"));
        assertTrue(rendered.contains("${username}"));
    }

    @Test
    public void testMultipleStopNodes() {
        FtlExpr expr1 = new LiteralExpr("First stop");
        FtlExpr expr2 = new LiteralExpr("Second stop");
        
        List<FtlNode> nodes = Arrays.asList(
            new StopNode(expr1),
            new TextNode("Content"),
            new StopNode(expr2)
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("First stop"));
        assertTrue(rendered.contains("Second stop"));
        assertTrue(rendered.contains("Content"));
    }
}