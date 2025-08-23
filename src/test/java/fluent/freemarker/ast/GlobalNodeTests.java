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

public class GlobalNodeTests {

    @Test
    public void testGlobalNodeCreation() {
        FtlExpr expr = new LiteralExpr("global value");
        GlobalNode globalNode = new GlobalNode("globalVar", expr);
        
        assertNotNull(globalNode);
        assertEquals("globalVar", globalNode.var);
        assertNotNull(globalNode.expr);
        assertEquals("global value", ((LiteralExpr)globalNode.expr).getValue());
    }

    @Test
    public void testGlobalNodeSerialization() throws Exception {
        FtlExpr expr = new LiteralExpr("test global");
        GlobalNode globalNode = new GlobalNode("testGlobal", expr);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(globalNode));
        assertNotNull(json);
        assertTrue(json.contains("testGlobal"));
        assertTrue(json.contains("test global"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof GlobalNode);
        
        GlobalNode deserializedGlobal = (GlobalNode) deserializedNodes.get(0);
        assertEquals("testGlobal", deserializedGlobal.var);
        assertNotNull(deserializedGlobal.expr);
    }

    @Test
    public void testGlobalNodeRendering() {
        FtlExpr expr = new LiteralExpr("Global Configuration");
        GlobalNode globalNode = new GlobalNode("config", expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(globalNode));
        assertTrue(rendered.contains("<#global"));
        assertTrue(rendered.contains("config"));
        assertTrue(rendered.contains("Global Configuration"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testGlobalNodeToString() {
        FtlExpr expr = new LiteralExpr("string test");
        GlobalNode globalNode = new GlobalNode("testVar", expr);
        
        String toString = globalNode.toString();
        assertNotNull(toString);
        // GlobalNode doesn't override toString, so it should use Object's default
    }

    @Test
    public void testGlobalNodeVisitorPattern() {
        FtlExpr expr = new LiteralExpr("visitor test");
        GlobalNode globalNode = new GlobalNode("visitorVar", expr);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(GlobalNode node) {
                assertNotNull(node);
                assertEquals("visitorVar", node.var);
                assertNotNull(node.expr);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> globalNode.accept(visitor));
    }

    @Test
    public void testGlobalNodeInTemplate() {
        FtlExpr expr = new LiteralExpr("application title");
        GlobalNode globalNode = new GlobalNode("appTitle", expr);
        
        // Test global node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            globalNode,
            new TextNode("Title: "),
            new VarNode("appTitle")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("application title"));
        assertTrue(rendered.contains("Title: ${appTitle}"));
    }

    @Test
    public void testGlobalNodeWithNullExpression() {
        GlobalNode globalNode = new GlobalNode("nullVar", null);
        
        assertNotNull(globalNode);
        assertEquals("nullVar", globalNode.var);
        assertNull(globalNode.expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(globalNode));
        assertTrue(rendered.contains("<#global"));
        assertTrue(rendered.contains("nullVar"));
    }

    @Test
    public void testGlobalNodeWithComplexExpression() {
        FtlExpr expr = new LiteralExpr("user.name + ' - ' + user.role");
        GlobalNode globalNode = new GlobalNode("userInfo", expr);
        
        assertEquals("userInfo", globalNode.var);
        assertNotNull(globalNode.expr);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(globalNode));
        assertTrue(rendered.contains("userInfo"));
        assertTrue(rendered.contains("user.name + ' - ' + user.role"));
    }

    @Test
    public void testMultipleGlobalNodes() {
        FtlExpr expr1 = new LiteralExpr("value1");
        FtlExpr expr2 = new LiteralExpr("value2");
        
        List<FtlNode> nodes = Arrays.asList(
            new GlobalNode("global1", expr1),
            new GlobalNode("global2", expr2),
            new TextNode("Globals defined")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("global1"));
        assertTrue(rendered.contains("global2"));
        assertTrue(rendered.contains("value1"));
        assertTrue(rendered.contains("value2"));
        assertTrue(rendered.contains("Globals defined"));
    }
}