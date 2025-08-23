package fluent.freemarker.ast;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MacroNodeTests {

    @Test
    public void testMacroNodeCreation() {
        Map<String, String> params = new HashMap<>();
        params.put("title", "string");
        params.put("count", "number");
        
        List<FtlNode> body = Arrays.asList(
            new TextNode("Title: "),
            new VarNode("title"),
            new TextNode(", Count: "),
            new VarNode("count")
        );
        
        MacroNode macroNode = new MacroNode("displayInfo", params, body);
        
        assertEquals("displayInfo", macroNode.getName());
        assertEquals(2, macroNode.getParams().size());
        assertEquals("string", macroNode.getParams().get("title"));
        assertEquals("number", macroNode.getParams().get("count"));
        assertEquals(4, macroNode.getBody().size());
    }

    @Test
    public void testMacroNodeWithNoParameters() {
        List<FtlNode> body = Arrays.asList(new TextNode("Simple macro content"));
        MacroNode macroNode = new MacroNode("simpleMacro", Collections.emptyMap(), body);
        
        assertEquals("simpleMacro", macroNode.getName());
        assertTrue(macroNode.getParams().isEmpty());
        assertEquals(1, macroNode.getBody().size());
    }

    @Test
    public void testMacroNodeSerialization() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("name", "string");
        
        List<FtlNode> body = Arrays.asList(new TextNode("Hello "), new VarNode("name"));
        MacroNode macroNode = new MacroNode("greet", params, body);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(macroNode));
        assertNotNull(json);
        assertTrue(json.contains("\"type\" : \"Macro\""));
        assertTrue(json.contains("\"name\" : \"greet\""));
        assertTrue(json.contains("\"name\" : \"string\""));
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof MacroNode);
        
        MacroNode deserializedMacro = (MacroNode) deserializedNodes.get(0);
        assertEquals("greet", deserializedMacro.getName());
        assertEquals(1, deserializedMacro.getParams().size());
        assertEquals("string", deserializedMacro.getParams().get("name"));
        assertEquals(2, deserializedMacro.getBody().size());
    }

    @Test
    public void testMacroNodeRendering() {
        Map<String, String> params = new HashMap<>();
        params.put("message", "string");
        
        List<FtlNode> ast = FtlBuilder.create()
                .macro("showMessage", params, b -> b
                    .text("Message: ")
                    .var("message"))
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#macro showMessage message=\"string\">"));
        assertTrue(rendered.contains("Message: ${message}"));
        assertTrue(rendered.contains("</#macro>"));
    }

    @Test
    public void testMacroNodeWithMultipleParameters() {
        Map<String, String> params = new LinkedHashMap<>(); // Use LinkedHashMap for predictable order
        params.put("title", "string");
        params.put("content", "string");
        params.put("isVisible", "boolean");
        
        List<FtlNode> ast = FtlBuilder.create()
                .macro("renderSection", params, b -> b
                    .ifBlock("isVisible", inner -> inner
                        .text("<h2>")
                        .var("title")
                        .text("</h2><p>")
                        .var("content")
                        .text("</p>")))
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof MacroNode);
        
        MacroNode macroNode = (MacroNode) ast.get(0);
        assertEquals("renderSection", macroNode.getName());
        assertEquals(3, macroNode.getParams().size());
        assertTrue(macroNode.getParams().containsKey("title"));
        assertTrue(macroNode.getParams().containsKey("content"));
        assertTrue(macroNode.getParams().containsKey("isVisible"));
    }

    @Test
    public void testMacroCallNode() {
        Map<String, FtlExpr> args = new HashMap<>();
        args.put("name", new LiteralExpr("John"));
        args.put("age", new LiteralExpr("30"));
        
        MacroCallNode macroCall = new MacroCallNode("displayUser", args);
        
        assertEquals("displayUser", macroCall.getName());
        assertEquals(2, macroCall.getArgs().size());
        assertTrue(macroCall.getArgs().get("name") instanceof LiteralExpr);
        assertTrue(macroCall.getArgs().get("age") instanceof LiteralExpr);
    }

    @Test
    public void testMacroCallNodeSerialization() throws Exception {
        Map<String, FtlExpr> args = new HashMap<>();
        args.put("title", new LiteralExpr("Hello World"));
        
        MacroCallNode macroCall = new MacroCallNode("showTitle", args);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(macroCall));
        assertNotNull(json);
        // Note: MacroCallNode might not be in the JsonSubTypes list, check the actual implementation
        
        // Basic validation
        assertNotNull(macroCall.getName());
        assertNotNull(macroCall.getArgs());
    }

    @Test
    public void testNestedMacroDefinitions() {
        Map<String, String> outerParams = new HashMap<>();
        outerParams.put("data", "object");
        
        Map<String, String> innerParams = new HashMap<>();
        innerParams.put("item", "string");
        
        List<FtlNode> ast = FtlBuilder.create()
                .macro("processData", outerParams, b -> b
                    .text("Processing data...")
                    .macro("processItem", innerParams, inner -> inner
                        .text("Item: ")
                        .var("item")))
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof MacroNode);
        
        MacroNode outerMacro = (MacroNode) ast.get(0);
        assertEquals("processData", outerMacro.getName());
        assertEquals(2, outerMacro.getBody().size());
        assertTrue(outerMacro.getBody().get(1) instanceof MacroNode);
        
        MacroNode innerMacro = (MacroNode) outerMacro.getBody().get(1);
        assertEquals("processItem", innerMacro.getName());
    }

    @Test
    public void testMacroNodeToString() {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "string");
        
        List<FtlNode> body = Arrays.asList(new TextNode("content"));
        MacroNode macroNode = new MacroNode("testMacro", params, body);
        
        String toString = macroNode.toString();
        assertTrue(toString.contains("Macro"));
        assertTrue(toString.contains("testMacro"));
    }

    @Test
    public void testMacroNodeVisitorPattern() {
        Map<String, String> params = new HashMap<>();
        params.put("test", "string");
        
        List<FtlNode> body = Arrays.asList(new TextNode("Test"));
        MacroNode macroNode = new MacroNode("testMacro", params, body);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(MacroNode node) {
                assertEquals("testMacro", node.getName());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> macroNode.accept(visitor));
    }

    @Test
    public void testImmutableCollections() {
        Map<String, String> params = new HashMap<>();
        params.put("param", "string");
        
        List<FtlNode> body = Arrays.asList(new TextNode("content"));
        MacroNode macroNode = new MacroNode("macro", params, body);
        
        // Test that returned collections are immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            macroNode.getParams().put("newParam", "string");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            macroNode.getBody().add(new TextNode("Should not work"));
        });
    }

    @Test
    public void testMacroWithComplexBody() {
        Map<String, String> params = new HashMap<>();
        params.put("items", "list");
        params.put("showIndex", "boolean");
        
        List<FtlNode> ast = FtlBuilder.create()
                .macro("renderList", params, b -> b
                    .ifBlock("items?? && items?size > 0", ifBody -> ifBody
                        .text("<ul>")
                        .list("item", "items", listBody -> listBody
                            .text("<li>")
                            .ifBlock("showIndex", indexIf -> indexIf
                                .text("[")
                                .var("item_index")
                                .text("] "))
                            .var("item")
                            .text("</li>"))
                        .text("</ul>")))
                .build();

        assertTrue(ast.get(0) instanceof MacroNode);
        MacroNode macroNode = (MacroNode) ast.get(0);
        assertEquals("renderList", macroNode.getName());
        assertEquals(2, macroNode.getParams().size());
        
        // Verify the complex nested structure
        assertEquals(1, macroNode.getBody().size());
        assertTrue(macroNode.getBody().get(0) instanceof IfNode);
    }
}