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

public class CaseNodeTests {

    @Test
    public void testCaseNodeCreation() {
        List<FtlExpr> values = Arrays.asList(new LiteralExpr("value1"), new LiteralExpr("value2"));
        List<FtlNode> body = Arrays.asList(new TextNode("Case body"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        assertNotNull(caseNode.values);
        assertNotNull(caseNode.body);
        assertEquals(2, caseNode.values.size());
        assertEquals(1, caseNode.body.size());
        assertEquals("value1", ((LiteralExpr)caseNode.values.get(0)).getValue());
        assertEquals("value2", ((LiteralExpr)caseNode.values.get(1)).getValue());
    }

    @Test
    public void testCaseNodeSerialization() throws Exception {
        List<FtlExpr> values = Collections.singletonList(new LiteralExpr("testValue"));
        List<FtlNode> body = Collections.singletonList(new TextNode("Test case"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(caseNode));
        assertNotNull(json);
        assertTrue(json.contains("testValue"));
        assertTrue(json.contains("Test case"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof CaseNode);
        
        CaseNode deserializedCase = (CaseNode) deserializedNodes.get(0);
        assertEquals(1, deserializedCase.values.size());
        assertEquals(1, deserializedCase.body.size());
    }

    @Test
    public void testCaseNodeRendering() {
        List<FtlExpr> values = Arrays.asList(new LiteralExpr("option1"), new LiteralExpr("option2"));
        List<FtlNode> body = Arrays.asList(new TextNode("Selected option"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(caseNode));
        assertTrue(rendered.contains("<#case"));
        assertTrue(rendered.contains("option1"));
        assertTrue(rendered.contains("option2"));
        assertTrue(rendered.contains("Selected option"));
    }

    @Test
    public void testCaseNodeToString() {
        List<FtlExpr> values = Collections.singletonList(new LiteralExpr("test"));
        List<FtlNode> body = Collections.singletonList(new TextNode("body"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        String toString = caseNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Case{"));
        assertTrue(toString.contains("test"));
    }

    @Test
    public void testCaseNodeVisitorPattern() {
        List<FtlExpr> values = Collections.singletonList(new LiteralExpr("value"));
        List<FtlNode> body = Collections.singletonList(new TextNode("body"));
        
        CaseNode caseNode = new CaseNode(values, body);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(CaseNode node) {
                assertNotNull(node.values);
                assertNotNull(node.body);
                assertEquals(1, node.values.size());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> caseNode.accept(visitor));
    }

    @Test
    public void testCaseNodeWithEmptyBody() {
        List<FtlExpr> values = Collections.singletonList(new LiteralExpr("emptyCase"));
        List<FtlNode> body = Collections.emptyList();
        
        CaseNode caseNode = new CaseNode(values, body);
        
        assertEquals(1, caseNode.values.size());
        assertEquals(0, caseNode.body.size());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(caseNode));
        assertTrue(rendered.contains("<#case"));
        assertTrue(rendered.contains("emptyCase"));
    }

    @Test
    public void testCaseNodeWithMultipleValues() {
        List<FtlExpr> values = Arrays.asList(
            new LiteralExpr("val1"),
            new LiteralExpr("val2"),
            new LiteralExpr("val3")
        );
        List<FtlNode> body = Arrays.asList(
            new TextNode("Multiple values case: "),
            new VarNode("selectedValue")
        );
        
        CaseNode caseNode = new CaseNode(values, body);
        
        assertEquals(3, caseNode.values.size());
        assertEquals(2, caseNode.body.size());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(caseNode));
        assertTrue(rendered.contains("val1"));
        assertTrue(rendered.contains("val2"));
        assertTrue(rendered.contains("val3"));
        assertTrue(rendered.contains("Multiple values case"));
    }

    @Test
    public void testCaseNodeWithComplexBody() {
        List<FtlExpr> values = Collections.singletonList(new LiteralExpr("complex"));
        List<FtlNode> body = Arrays.asList(
            new TextNode("Complex case with "),
            new VarNode("userName"),
            new TextNode(" and "),
            new VarNode("userAge")
        );
        
        CaseNode caseNode = new CaseNode(values, body);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(caseNode));
        assertTrue(rendered.contains("Complex case with"));
        assertTrue(rendered.contains("${userName}"));
        assertTrue(rendered.contains("${userAge}"));
    }
}