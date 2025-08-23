package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompressNodeTests {

    @Test
    public void testCompressNodeCreation() {
        List<FtlNode> body = Arrays.asList(
            new TextNode("  Whitespace will be compressed  "),
            new VarNode("content")
        );
        
        CompressNode compressNode = new CompressNode(body);
        
        assertNotNull(compressNode.getBody());
        assertEquals(2, compressNode.getBody().size());
    }

    @Test
    public void testCompressNodeSerialization() throws Exception {
        List<FtlNode> body = Collections.singletonList(new TextNode("Compressed content"));
        
        CompressNode compressNode = new CompressNode(body);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(compressNode));
        assertNotNull(json);
        assertTrue(json.contains("Compressed content"));

        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof CompressNode);
        
        CompressNode deserializedCompress = (CompressNode) deserializedNodes.get(0);
        assertEquals(1, deserializedCompress.getBody().size());
    }

    @Test
    public void testCompressNodeRendering() {
        List<FtlNode> body = Arrays.asList(
            new TextNode("  Multiple   "),
            new TextNode("   spaces   "),
            new TextNode("  here  ")
        );
        
        CompressNode compressNode = new CompressNode(body);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(compressNode));
        assertTrue(rendered.contains("<#compress>"));
        assertTrue(rendered.contains("Multiple"));
        assertTrue(rendered.contains("spaces"));
        assertTrue(rendered.contains("here"));
        assertTrue(rendered.contains("</#compress>"));
    }

    @Test
    public void testCompressNodeToString() {
        List<FtlNode> body = Collections.singletonList(new TextNode("test content"));
        
        CompressNode compressNode = new CompressNode(body);
        
        String toString = compressNode.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Compress{"));
        assertTrue(toString.contains("test content"));
    }

    @Test
    public void testCompressNodeVisitorPattern() {
        List<FtlNode> body = Collections.singletonList(new TextNode("visitor test"));
        
        CompressNode compressNode = new CompressNode(body);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(CompressNode node) {
                assertNotNull(node.getBody());
                assertEquals(1, node.getBody().size());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> compressNode.accept(visitor));
    }

    @Test
    public void testCompressNodeWithEmptyBody() {
        List<FtlNode> body = Collections.emptyList();
        
        CompressNode compressNode = new CompressNode(body);
        
        assertEquals(0, compressNode.getBody().size());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(compressNode));
        assertTrue(rendered.contains("<#compress>"));
        assertTrue(rendered.contains("</#compress>"));
    }

    @Test
    public void testCompressNodeWithComplexContent() {
        List<FtlNode> body = Arrays.asList(
            new TextNode("   Start with spaces   "),
            new VarNode("username"),
            new TextNode("   middle spaces   "),
            new VarNode("email"),
            new TextNode("   end with spaces   ")
        );
        
        CompressNode compressNode = new CompressNode(body);
        
        assertEquals(5, compressNode.getBody().size());
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(compressNode));
        assertTrue(rendered.contains("Start with spaces"));
        assertTrue(rendered.contains("${username}"));
        assertTrue(rendered.contains("middle spaces"));
        assertTrue(rendered.contains("${email}"));
        assertTrue(rendered.contains("end with spaces"));
    }

    @Test
    public void testCompressNodeNested() {
        List<FtlNode> innerBody = Arrays.asList(
            new TextNode("  inner  "),
            new VarNode("value")
        );
        CompressNode innerCompress = new CompressNode(innerBody);
        
        List<FtlNode> outerBody = Arrays.asList(
            new TextNode("  outer  "),
            innerCompress
        );
        CompressNode outerCompress = new CompressNode(outerBody);
        
        assertEquals(2, outerCompress.getBody().size());
        assertTrue(outerCompress.getBody().get(1) instanceof CompressNode);
        
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(outerCompress));
        assertTrue(rendered.contains("outer"));
        assertTrue(rendered.contains("inner"));
        assertTrue(rendered.contains("${value}"));
    }
}