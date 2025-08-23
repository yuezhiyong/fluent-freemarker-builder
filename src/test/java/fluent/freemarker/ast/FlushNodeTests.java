package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlushNodeTests {

    @Test
    public void testFlushNodeCreation() {
        FlushNode flushNode = new FlushNode();
        
        assertNotNull(flushNode);
    }

    @Test
    public void testFlushNodeSerialization() throws Exception {
        FlushNode flushNode = new FlushNode();
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(flushNode));
        assertNotNull(json);
        assertTrue(json.contains("Flush"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof FlushNode);
    }

    @Test
    public void testFlushNodeRendering() {
        FlushNode flushNode = new FlushNode();
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(flushNode));
        assertTrue(rendered.contains("<#flush"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testFlushNodeToString() {
        FlushNode flushNode = new FlushNode();
        
        String toString = flushNode.toString();
        assertNotNull(toString);
        assertEquals("Flush", toString);
    }

    @Test
    public void testFlushNodeVisitorPattern() {
        FlushNode flushNode = new FlushNode();
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(FlushNode node) {
                assertNotNull(node);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> flushNode.accept(visitor));
    }

    @Test
    public void testFlushNodeInTemplate() {
        // Test flush node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before flush"),
            new FlushNode(),
            new TextNode("After flush")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before flush"));
        assertTrue(rendered.contains("<#flush>"));
        assertTrue(rendered.contains("After flush"));
    }

    @Test
    public void testMultipleFlushNodes() {
        List<FtlNode> nodes = Arrays.asList(
            new FlushNode(),
            new TextNode("Content"),
            new FlushNode()
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Content"));
        // Should contain two flush directives
        int flushCount = rendered.split("<#flush>").length - 1;
        assertEquals(2, flushCount);
    }
}