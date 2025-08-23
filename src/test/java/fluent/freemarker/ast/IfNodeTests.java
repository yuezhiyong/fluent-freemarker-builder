package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IfNodeTests {

    @Test
    public void testIfNodeCreation() {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Then content"));
        List<FtlNode> elseBlock = Arrays.asList(new TextNode("Else content"));
        
        IfNode ifNode = new IfNode("user.isActive", thenBlock, elseBlock);
        
        assertEquals("user.isActive", ifNode.getCondition());
        assertEquals(1, ifNode.getThenBlock().size());
        assertEquals(1, ifNode.getElseBlock().size());
        assertTrue(ifNode.getThenBlock().get(0) instanceof TextNode);
        assertTrue(ifNode.getElseBlock().get(0) instanceof TextNode);
    }

    @Test
    public void testIfNodeWithoutElse() {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Only then"));
        
        IfNode ifNode = new IfNode("condition", thenBlock, null);
        
        assertEquals("condition", ifNode.getCondition());
        assertEquals(1, ifNode.getThenBlock().size());
        assertTrue(ifNode.getElseBlock().isEmpty());
    }

    @Test
    public void testIfNodeSerialization() throws Exception {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Active user"));
        IfNode ifNode = new IfNode("user.active", thenBlock, null);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(ifNode));
        assertNotNull(json);
        assertTrue(json.contains("\"type\":\"If\""));
        assertTrue(json.contains("\"condition\":\"user.active\""));
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof IfNode);
        
        IfNode deserializedIf = (IfNode) deserializedNodes.get(0);
        assertEquals("user.active", deserializedIf.getCondition());
        assertEquals(1, deserializedIf.getThenBlock().size());
    }

    @Test
    public void testIfNodeRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .ifBlock("user.isAdmin", b -> b.text("Admin content"))
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#if user.isAdmin>"));
        assertTrue(rendered.contains("Admin content"));
        assertTrue(rendered.contains("</#if>"));
    }

    @Test
    public void testIfElseNodeRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .ifElseBlock("user.isActive", 
                    b -> b.text("User is active"), 
                    b -> b.text("User is inactive"))
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#if user.isActive>"));
        assertTrue(rendered.contains("User is active"));
        assertTrue(rendered.contains("<#else>"));
        assertTrue(rendered.contains("User is inactive"));
        assertTrue(rendered.contains("</#if>"));
    }

    @Test
    public void testNestedIfNodes() {
        List<FtlNode> ast = FtlBuilder.create()
                .ifBlock("user.isLoggedIn", b -> b
                    .text("Welcome ")
                    .var("user.name")
                    .ifBlock("user.isPremium", inner -> inner
                        .text(" - Premium Member")))
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof IfNode);
        
        IfNode outerIf = (IfNode) ast.get(0);
        assertEquals("user.isLoggedIn", outerIf.getCondition());
        assertEquals(3, outerIf.getThenBlock().size());
        
        // Check the nested if
        assertTrue(outerIf.getThenBlock().get(2) instanceof IfNode);
        IfNode innerIf = (IfNode) outerIf.getThenBlock().get(2);
        assertEquals("user.isPremium", innerIf.getCondition());
    }

    @Test
    public void testIfNodeWithComplexCondition() {
        List<FtlNode> ast = FtlBuilder.create()
                .ifBlock("(user.age >= 18) && user.verified", b -> b
                    .text("Access granted"))
                .build();

        assertTrue(ast.get(0) instanceof IfNode);
        IfNode ifNode = (IfNode) ast.get(0);
        assertEquals("(user.age >= 18) && user.verified", ifNode.getCondition());
    }

    @Test
    public void testIfNodeToString() {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Content"));
        IfNode ifNode = new IfNode("testCondition", thenBlock, null);
        
        String toString = ifNode.toString();
        assertTrue(toString.contains("If(testCondition)"));
    }

    @Test
    public void testIfNodeVisitorPattern() {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Test"));
        IfNode ifNode = new IfNode("test", thenBlock, null);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(IfNode node) {
                assertEquals("test", node.getCondition());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> ifNode.accept(visitor));
    }

    @Test
    public void testImmutableCollections() {
        List<FtlNode> thenBlock = Arrays.asList(new TextNode("Then"));
        List<FtlNode> elseBlock = Arrays.asList(new TextNode("Else"));
        
        IfNode ifNode = new IfNode("condition", thenBlock, elseBlock);
        
        // Test that returned collections are immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            ifNode.getThenBlock().add(new TextNode("Should not work"));
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            ifNode.getElseBlock().add(new TextNode("Should not work"));
        });
    }
}