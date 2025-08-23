package fluent.freemarker.ast;

import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignNodeTests {

    @Test
    public void testAssignNodeCreation() {
        AssignNode assignNode = new AssignNode("userName", "John Doe");
        
        assertEquals("userName", assignNode.getVarName());
        assertEquals("John Doe", assignNode.getValueExpr());
    }

    @Test
    public void testAssignNodeSerialization() throws Exception {
        AssignNode assignNode = new AssignNode("testVar", "test value");
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(assignNode));
        assertNotNull(json);
        // AssignNode doesn't include type in JSON for some reason, just check the content
        assertTrue(json.contains("testVar"));
        assertTrue(json.contains("test value"));
        

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof AssignNode);
        
        AssignNode deserializedAssign = (AssignNode) deserializedNodes.get(0);
        assertEquals("testVar", deserializedAssign.getVarName());
        assertEquals("test value", deserializedAssign.getValueExpr());
    }

    @Test
    public void testAssignNodeRendering() {
        AssignNode assignNode = new AssignNode("greeting", "Hello World");
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(assignNode));
        assertTrue(rendered.contains("<#assign"));
        assertTrue(rendered.contains("greeting"));
        assertTrue(rendered.contains("Hello World"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testAssignNodeWithComplexExpression() {
        AssignNode assignNode = new AssignNode("fullName", "user.firstName + ' ' + user.lastName");
        
        assertEquals("fullName", assignNode.getVarName());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(assignNode));
        assertTrue(rendered.contains("fullName"));
        assertTrue(rendered.contains("user.firstName + ' ' + user.lastName"));
    }

    @Test
    public void testAssignNodeInTemplate() {
        // Note: FtlBuilder might not have direct assign support, so we test the node directly
        AssignNode assignNode1 = new AssignNode("title", "Welcome");
        AssignNode assignNode2 = new AssignNode("count", "42");
        
        List<FtlNode> nodes = Arrays.asList(
            assignNode1,
            new TextNode("Title: "),
            new VarNode("title"),
            new TextNode(", Count: "),
            new VarNode("count"),
            assignNode2
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("<#assign title"));
        assertTrue(rendered.contains("<#assign count"));
        assertTrue(rendered.contains("Title: ${title}"));
        assertTrue(rendered.contains("Count: ${count}"));
    }

    @Test
    public void testAssignNodeToString() {
        AssignNode assignNode = new AssignNode("testVar", "testValue");
        
        String toString = assignNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Assign") || toString.contains("testVar"));
    }

    @Test
    public void testAssignNodeVisitorPattern() {
        AssignNode assignNode = new AssignNode("test", "value");
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(AssignNode node) {
                assertEquals("test", node.getVarName());
                assertEquals("value", node.getValueExpr());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> assignNode.accept(visitor));
    }

    @Test
    public void testMultipleAssignments() {
        AssignNode assign1 = new AssignNode("firstName", "John");
        AssignNode assign2 = new AssignNode("lastName", "Smith");
        AssignNode assign3 = new AssignNode("fullName", "firstName + ' ' + lastName");
        
        List<FtlNode> nodes = Arrays.asList(assign1, assign2, assign3);
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("firstName"));
        assertTrue(rendered.contains("lastName"));
        assertTrue(rendered.contains("fullName"));
    }

    @Test
    public void testAssignNodeWithNumericValue() {
        AssignNode assignNode = new AssignNode("age", "25");
        
        assertEquals("age", assignNode.getVarName());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(assignNode));
        assertTrue(rendered.contains("age"));
        assertTrue(rendered.contains("25"));
    }

    @Test
    public void testAssignNodeWithBooleanValue() {
        AssignNode assignNode = new AssignNode("isActive", "true");
        
        assertEquals("isActive", assignNode.getVarName());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(assignNode));
        assertTrue(rendered.contains("isActive"));
        assertTrue(rendered.contains("true"));
    }

    @Test
    public void testAssignNodeWithVariableReference() {
        AssignNode assignNode = new AssignNode("currentUser", "session.user");
        
        assertEquals("currentUser", assignNode.getVarName());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(assignNode));
        assertTrue(rendered.contains("currentUser"));
        assertTrue(rendered.contains("session.user"));
    }
}