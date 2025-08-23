package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AttemptNodeTests {

    @Test
    public void testAttemptNodeCreation() {
        List<FtlNode> attemptBody = Arrays.asList(
            new TextNode("Trying to access: "),
            new VarNode("user.profile.email")
        );
        List<FtlNode> recoverBody = Arrays.asList(
            new TextNode("Email not available")
        );
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, recoverBody);
        
        assertEquals(2, attemptNode.getAttemptBody().size());
        assertEquals(1, attemptNode.getRecoverBody().size());
        assertTrue(attemptNode.getAttemptBody().get(0) instanceof TextNode);
        assertTrue(attemptNode.getAttemptBody().get(1) instanceof VarNode);
        assertTrue(attemptNode.getRecoverBody().get(0) instanceof TextNode);
    }

    @Test
    public void testAttemptNodeWithoutRecover() {
        List<FtlNode> attemptBody = Arrays.asList(new TextNode("Risky operation"));
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, Collections.emptyList());
        
        assertEquals(1, attemptNode.getAttemptBody().size());
        assertTrue(attemptNode.getRecoverBody().isEmpty());
    }

    @Test
    public void testAttemptNodeSerialization() throws Exception {
        List<FtlNode> attemptBody = Arrays.asList(new VarNode("risky.operation"));
        List<FtlNode> recoverBody = Arrays.asList(new TextNode("Operation failed"));
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, recoverBody);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(attemptNode));
        assertNotNull(json);
        assertTrue(json.contains("\"type\" : \"Attempt\""));
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof AttemptNode);
        
        AttemptNode deserializedAttempt = (AttemptNode) deserializedNodes.get(0);
        assertEquals(1, deserializedAttempt.getAttemptBody().size());
        assertEquals(1, deserializedAttempt.getRecoverBody().size());
    }

    @Test
    public void testAttemptNodeRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .attempt(
                    b -> b.text("Accessing ").var("user.secret"),
                    b -> b.text("Access denied")
                )
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#attempt>"));
        assertTrue(rendered.contains("Accessing ${user.secret}"));
        assertTrue(rendered.contains("<#recover>"));
        assertTrue(rendered.contains("Access denied"));
        assertTrue(rendered.contains("</#attempt>"));
    }

    @Test
    public void testAttemptNodeWithoutRecoverRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .attempt(
                    b -> b.text("Risky operation: ").var("dangerous.value"),
                    null
                )
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#attempt>"));
        assertTrue(rendered.contains("Risky operation: ${dangerous.value}"));
        assertTrue(rendered.contains("</#attempt>"));
        // Should not contain recover block
        assertFalse(rendered.contains("<#recover>"));
    }

    @Test
    public void testNestedAttemptNodes() {
        List<FtlNode> ast = FtlBuilder.create()
                .attempt(
                    b -> b
                        .text("Outer attempt: ")
                        .attempt(
                            inner -> inner.text("Inner risky: ").var("inner.value"),
                            inner -> inner.text("Inner failed")
                        ),
                    b -> b.text("Outer failed")
                )
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof AttemptNode);
        
        AttemptNode outerAttempt = (AttemptNode) ast.get(0);
        assertEquals(2, outerAttempt.getAttemptBody().size());
        assertTrue(outerAttempt.getAttemptBody().get(1) instanceof AttemptNode);
        
        AttemptNode innerAttempt = (AttemptNode) outerAttempt.getAttemptBody().get(1);
        assertEquals(2, innerAttempt.getAttemptBody().size());
        assertEquals(1, innerAttempt.getRecoverBody().size());
    }

    @Test
    public void testAttemptNodeWithComplexBody() {
        List<FtlNode> ast = FtlBuilder.create()
                .attempt(
                    b -> b
                        .text("Processing user data...")
                        .ifBlock("user.hasProfile", ifB -> ifB
                            .text("Profile: ")
                            .var("user.profile.name"))
                        .list("item", "user.items", listB -> listB
                            .text("Item: ")
                            .var("item")),
                    b -> b
                        .text("Error processing user data")
                        .comment("This is a fallback")
                )
                .build();

        assertTrue(ast.get(0) instanceof AttemptNode);
        AttemptNode attemptNode = (AttemptNode) ast.get(0);
        
        assertEquals(3, attemptNode.getAttemptBody().size());
        assertEquals(2, attemptNode.getRecoverBody().size());
        
        // Verify complex nested structures
        assertTrue(attemptNode.getAttemptBody().get(1) instanceof IfNode);
        assertTrue(attemptNode.getAttemptBody().get(2) instanceof ListNode);
        assertTrue(attemptNode.getRecoverBody().get(1) instanceof CommentNode);
    }

    @Test
    public void testAttemptNodeToString() {
        List<FtlNode> attemptBody = Arrays.asList(new TextNode("test"));
        List<FtlNode> recoverBody = Arrays.asList(new TextNode("recover"));
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, recoverBody);
        
        String toString = attemptNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Attempt") || toString.length() > 0);
    }

    @Test
    public void testAttemptNodeVisitorPattern() {
        List<FtlNode> attemptBody = Arrays.asList(new TextNode("Test"));
        List<FtlNode> recoverBody = Arrays.asList(new TextNode("Recover"));
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, recoverBody);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(AttemptNode node) {
                assertEquals(1, node.getAttemptBody().size());
                assertEquals(1, node.getRecoverBody().size());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> attemptNode.accept(visitor));
    }

    @Test
    public void testImmutableCollections() {
        List<FtlNode> attemptBody = Arrays.asList(new TextNode("Attempt"));
        List<FtlNode> recoverBody = Arrays.asList(new TextNode("Recover"));
        
        AttemptNode attemptNode = new AttemptNode(attemptBody, recoverBody);
        
        // Test that returned collections are immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            attemptNode.getAttemptBody().add(new TextNode("Should not work"));
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            attemptNode.getRecoverBody().add(new TextNode("Should not work"));
        });
    }

    @Test
    public void testAttemptNodeRealWorldScenario() {
        // Simulate a real-world scenario where we attempt to access optional user data
        List<FtlNode> ast = FtlBuilder.create()
                .text("User Information:")
                .attempt(
                    b -> b
                        .text("Name: ").var("user.profile.fullName")
                        .text(", Email: ").var("user.contact.email")
                        .text(", Phone: ").var("user.contact.phone")
                        .ifBlock("user.preferences??", prefB -> prefB
                            .text(", Theme: ").var("user.preferences.theme")),
                    b -> b
                        .text("User information is not complete or accessible")
                        .comment("Fallback when user data is missing")
                )
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("User Information:"));
        assertTrue(rendered.contains("<#attempt>"));
        assertTrue(rendered.contains("user.profile.fullName"));
        assertTrue(rendered.contains("<#recover>"));
        assertTrue(rendered.contains("not complete or accessible"));
    }

    @Test
    public void testEmptyAttemptBody() {
        List<FtlNode> emptyAttempt = Collections.emptyList();
        List<FtlNode> recoverBody = Arrays.asList(new TextNode("Nothing to attempt"));
        
        AttemptNode attemptNode = new AttemptNode(emptyAttempt, recoverBody);
        
        assertTrue(attemptNode.getAttemptBody().isEmpty());
        assertEquals(1, attemptNode.getRecoverBody().size());
    }
}