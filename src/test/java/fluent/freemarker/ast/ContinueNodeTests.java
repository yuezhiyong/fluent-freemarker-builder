package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContinueNodeTests {

    @Test
    public void testContinueNodeCreation() {
        ContinueNode continueNode = new ContinueNode();
        
        assertNotNull(continueNode);
    }

    @Test
    public void testContinueNodeSerialization() throws Exception {
        ContinueNode continueNode = new ContinueNode();
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(continueNode));
        assertNotNull(json);
        assertTrue(json.contains("Continue"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof ContinueNode);
    }

    @Test
    public void testContinueNodeRendering() {
        ContinueNode continueNode = new ContinueNode();
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(continueNode));
        assertTrue(rendered.contains("<#continue"));
        assertTrue(rendered.contains(">"));
    }

    @Test
    public void testContinueNodeToString() {
        ContinueNode continueNode = new ContinueNode();
        
        String toString = continueNode.toString();
        assertNotNull(toString);
        assertEquals("Continue", toString);
    }

    @Test
    public void testContinueNodeVisitorPattern() {
        ContinueNode continueNode = new ContinueNode();
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(ContinueNode node) {
                assertNotNull(node);
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> continueNode.accept(visitor));
    }

    @Test
    public void testContinueNodeInLoop() {
        // Test continue node in context of other nodes
        List<FtlNode> nodes = Arrays.asList(
            new TextNode("Before continue"),
            new ContinueNode(),
            new TextNode("After continue")
        );
        
        String rendered = new FreeMarkerRenderer().render(nodes);
        assertTrue(rendered.contains("Before continue"));
        assertTrue(rendered.contains("<#continue>"));
        assertTrue(rendered.contains("After continue"));
    }
}