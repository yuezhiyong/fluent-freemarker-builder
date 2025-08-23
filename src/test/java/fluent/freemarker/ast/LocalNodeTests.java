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

public class LocalNodeTests {

    @Test
    public void testLocalNodeCreation() {
        FtlExpr expr = new LiteralExpr("local value");
        LocalNode localNode = new LocalNode("localVar", expr);
        
        assertNotNull(localNode);
        assertEquals("localVar", localNode.var);
        assertNotNull(localNode.expr);
        assertEquals("local value", ((LiteralExpr)localNode.expr).getValue());
    }

    @Test
    public void testLocalNodeSerialization() throws Exception {
        FtlExpr expr = new LiteralExpr("test local");
        LocalNode localNode = new LocalNode("testLocal", expr);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(localNode));
        assertNotNull(json);
        assertTrue(json.contains("testLocal"));
        assertTrue(json.contains("test local"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof LocalNode);
        
        LocalNode deserializedLocal = (LocalNode) deserializedNodes.get(0);
        assertEquals("testLocal", deserializedLocal.var);
        assertNotNull(deserializedLocal.expr);
    }

    @Test
    public void testLocalNodeRendering() {
        FtlExpr expr = new LiteralExpr("Local Variable");
        LocalNode localNode = new LocalNode("localVar", expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(localNode));
        assertTrue(rendered.contains("<#local"));
        assertTrue(rendered.contains("localVar"));
        assertTrue(rendered.contains("Local Variable"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testLocalNodeToString() {
        FtlExpr expr = new LiteralExpr("string test");
        LocalNode localNode = new LocalNode("testVar", expr);
        
        String toString = localNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Local{"));
        assertTrue(toString.contains("testVar"));
        assertTrue(toString.contains("string test"));
    }

    @Test
    public void testLocalNodeVisitorPattern() {
        FtlExpr expr = new LiteralExpr("visitor test");
        LocalNode localNode = new LocalNode("visitorVar", expr);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(LocalNode node) {
                assertNotNull(node);
                assertEquals("visitorVar", node.var);
                assertNotNull(node.expr);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> localNode.accept(visitor));
    }

    @Test
    public void testLocalNodeInTemplate() {
        FtlExpr expr = new LiteralExpr("temporary value");
        LocalNode localNode = new LocalNode("tempVar", expr);
        
        // Test local node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            localNode,
            new TextNode("Temp: "),
            new VarNode("tempVar")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("temporary value"));
        assertTrue(rendered.contains("Temp: ${tempVar}"));
    }

    @Test
    public void testLocalNodeWithNullExpression() {
        LocalNode localNode = new LocalNode("nullVar", null);
        
        assertNotNull(localNode);
        assertEquals("nullVar", localNode.var);
        assertNull(localNode.expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(localNode));
        assertTrue(rendered.contains("<#local"));
        assertTrue(rendered.contains("nullVar"));
    }

    @Test
    public void testLocalNodeWithComplexExpression() {
        FtlExpr expr = new LiteralExpr("item.price * quantity");
        LocalNode localNode = new LocalNode("totalPrice", expr);
        
        assertEquals("totalPrice", localNode.var);
        assertNotNull(localNode.expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(localNode));
        assertTrue(rendered.contains("totalPrice"));
        assertTrue(rendered.contains("item.price * quantity"));
    }

    @Test
    public void testMultipleLocalNodes() {
        FtlExpr expr1 = new LiteralExpr("first value");
        FtlExpr expr2 = new LiteralExpr("second value");
        
        List<FtlNode> nodes = Arrays.asList(
            new LocalNode("local1", expr1),
            new LocalNode("local2", expr2),
            new TextNode("Locals defined")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("local1"));
        assertTrue(rendered.contains("local2"));
        assertTrue(rendered.contains("first value"));
        assertTrue(rendered.contains("second value"));
        assertTrue(rendered.contains("Locals defined"));
    }

    @Test
    public void testLocalNodeScoping() {
        // Test that local variables are scoped properly
        FtlExpr expr = new LiteralExpr("scoped value");
        LocalNode localNode = new LocalNode("scopedVar", expr);
        
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before scope"),
            localNode,
            new VarNode("scopedVar"),
            new TextNode("After scope")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before scope"));
        assertTrue(rendered.contains("scoped value"));
        assertTrue(rendered.contains("${scopedVar}"));
        assertTrue(rendered.contains("After scope"));
    }
}