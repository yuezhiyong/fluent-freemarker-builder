package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VarNodeTests {

    @Test
    public void testVarNodeCreation() {
        VarNode varNode = new VarNode("user.name");
        
        assertEquals("user.name", varNode.getExpression());
        assertEquals("Var{user.name}", varNode.toString());
    }

    @Test
    public void testVarNodeSerialization() throws Exception {
        VarNode varNode = new VarNode("user.email");
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(varNode));
        assertEquals("[{\"type\":\"Var\",\"expression\":\"user.email\"}]", json);
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof VarNode);
        
        VarNode deserializedVar = (VarNode) deserializedNodes.get(0);
        assertEquals("user.email", deserializedVar.getExpression());
    }

    @Test
    public void testVarNodeRendering() {
        VarNode varNode = new VarNode("userName");
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(varNode));
        assertEquals("${userName}\n", rendered);
    }

    @Test
    public void testVarNodeWithComplexExpression() {
        VarNode varNode = new VarNode("user.profile.address.street");
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(varNode));
        assertEquals("${user.profile.address.street}\n", rendered);
    }

    @Test
    public void testVarNodeThroughBuilder() {
        List<FtlNode> ast = FtlBuilder.create()
                .var("productName")
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof VarNode);
        
        VarNode varNode = (VarNode) ast.get(0);
        assertEquals("productName", varNode.getExpression());
    }

    @Test
    public void testMultipleVarNodes() {
        List<FtlNode> ast = FtlBuilder.create()
                .text("Hello ")
                .var("firstName")
                .text(" ")
                .var("lastName")
                .build();

        assertEquals(4, ast.size());
        assertTrue(ast.get(1) instanceof VarNode);
        assertTrue(ast.get(3) instanceof VarNode);
        
        VarNode firstVar = (VarNode) ast.get(1);
        VarNode lastVar = (VarNode) ast.get(3);
        
        assertEquals("firstName", firstVar.getExpression());
        assertEquals("lastName", lastVar.getExpression());
    }

    @Test
    public void testVarNodeVisitorPattern() {
        VarNode varNode = new VarNode("testVar");
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(VarNode node) {
                assertEquals("testVar", node.getExpression());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> varNode.accept(visitor));
    }
}