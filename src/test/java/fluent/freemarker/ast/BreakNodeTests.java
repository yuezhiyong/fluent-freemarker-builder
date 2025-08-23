package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BreakNodeTests {

    @Test
    public void testBreakNodeCreation() {
        BreakNode breakNode = new BreakNode();
        
        assertNotNull(breakNode);
    }

    @Test
    public void testBreakNodeSerialization() throws Exception {
        BreakNode breakNode = new BreakNode();
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(breakNode));
        assertNotNull(json);
        assertTrue(json.contains("Break"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof BreakNode);
    }

    @Test
    public void testBreakNodeRendering() {
        BreakNode breakNode = new BreakNode();
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(breakNode));
        assertTrue(rendered.contains("<#break"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testBreakNodeToString() {
        BreakNode breakNode = new BreakNode();
        
        String toString = breakNode.toString();
        assertNotNull(toString);
        assertEquals("Break", toString);
    }

    @Test
    public void testBreakNodeVisitorPattern() {
        BreakNode breakNode = new BreakNode();
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(BreakNode node) {
                assertNotNull(node);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> breakNode.accept(visitor));
    }

    @Test
    public void testBreakNodeInLoop() {
        // Test break node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before break"),
            new BreakNode(),
            new TextNode("After break")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before break"));
        assertTrue(rendered.contains("<#break>"));
        assertTrue(rendered.contains("After break"));
    }
}