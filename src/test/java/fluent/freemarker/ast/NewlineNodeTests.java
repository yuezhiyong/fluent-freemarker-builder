package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewlineNodeTests {

    @Test
    public void testNewlineNodeInstance() {
        NewlineNode newlineNode = NewlineNode.INSTANCE;
        
        assertNotNull(newlineNode);
        assertSame(NewlineNode.INSTANCE, newlineNode);
    }

    @Test
    public void testNewlineNodeSingleton() {
        NewlineNode node1 = NewlineNode.INSTANCE;
        NewlineNode node2 = NewlineNode.INSTANCE;
        
        assertSame(node1, node2);
    }

    @Test
    public void testNewlineNodeSerialization() throws Exception {
        NewlineNode newlineNode = NewlineNode.INSTANCE;
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(newlineNode));
        assertNotNull(json);
        assertTrue(json.contains("Newline"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof NewlineNode);
    }

    @Test
    public void testNewlineNodeRendering() {
        NewlineNode newlineNode = NewlineNode.INSTANCE;
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(newlineNode));
        // Newline should render as actual newline character
        assertTrue(rendered.contains("\n") || rendered.contains("\r\n"));
    }

    @Test
    public void testNewlineNodeToString() {
        NewlineNode newlineNode = NewlineNode.INSTANCE;
        
        String toString = newlineNode.toString();
        assertNotNull(toString);
        assertEquals("Newline", toString);
    }

    @Test
    public void testNewlineNodeVisitorPattern() {
        NewlineNode newlineNode = NewlineNode.INSTANCE;
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(NewlineNode node) {
                assertNotNull(node);
                assertSame(NewlineNode.INSTANCE, node);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> newlineNode.accept(visitor));
    }

    @Test
    public void testNewlineNodeInTemplate() {
        // Test newline node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("First line"),
            NewlineNode.INSTANCE,
            new TextNode("Second line"),
            NewlineNode.INSTANCE,
            new TextNode("Third line")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("First line"));
        assertTrue(rendered.contains("Second line"));
        assertTrue(rendered.contains("Third line"));
        // Should contain newlines
        assertTrue(rendered.contains("\n") || rendered.contains("\r\n"));
    }

    @Test
    public void testMultipleNewlineNodes() {
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Line 1"),
            NewlineNode.INSTANCE,
            NewlineNode.INSTANCE,
            new TextNode("Line 2")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Line 1"));
        assertTrue(rendered.contains("Line 2"));
        // Should contain multiple newlines
        assertTrue(rendered.contains("\n") || rendered.contains("\r\n"));
    }

    @Test
    public void testNewlineNodeMultipleReferences() {
        // Test that multiple references to INSTANCE are the same
        List<FtlNode> nodes = Arrays.asList(
            NewlineNode.INSTANCE,
            NewlineNode.INSTANCE,
            NewlineNode.INSTANCE
        );
        
        // All should be the same instance
        assertSame(nodes.get(0), nodes.get(1));
        assertSame(nodes.get(1), nodes.get(2));
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertNotNull(rendered);
    }
}