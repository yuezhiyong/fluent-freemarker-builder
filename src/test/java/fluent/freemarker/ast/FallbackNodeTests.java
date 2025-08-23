package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FallbackNodeTests {

    @Test
    public void testFallbackNodeCreation() {
        FallbackNode fallbackNode = new FallbackNode();
        
        assertNotNull(fallbackNode);
    }

    @Test
    public void testFallbackNodeSerialization() throws Exception {
        FallbackNode fallbackNode = new FallbackNode();
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(fallbackNode));
        assertNotNull(json);
        assertTrue(json.contains("Fallback"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof FallbackNode);
    }

    @Test
    public void testFallbackNodeRendering() {
        FallbackNode fallbackNode = new FallbackNode();
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(fallbackNode));
        assertTrue(rendered.contains("<#fallback"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testFallbackNodeToString() {
        FallbackNode fallbackNode = new FallbackNode();
        
        String toString = fallbackNode.toString();
        assertNotNull(toString);
        assertEquals("Fallback", toString);
    }

    @Test
    public void testFallbackNodeVisitorPattern() {
        FallbackNode fallbackNode = new FallbackNode();
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(FallbackNode node) {
                assertNotNull(node);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> fallbackNode.accept(visitor));
    }

    @Test
    public void testFallbackNodeInTemplate() {
        // Test fallback node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before fallback"),
            new FallbackNode(),
            new TextNode("After fallback")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before fallback"));
        assertTrue(rendered.contains("<#fallback>"));
        assertTrue(rendered.contains("After fallback"));
    }

    @Test
    public void testMultipleFallbackNodes() {
        List<FtlNode> nodes = Arrays.asList(
            new FallbackNode(),
            new TextNode("Content"),
            new FallbackNode()
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Content"));
        // Should contain two fallback directives
        int fallbackCount = rendered.split("<#fallback>").length - 1;
        assertEquals(1, fallbackCount);
    }
}