package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListNodeTests {

    @Test
    public void testListNodeCreation() {
        List<FtlNode> body = Arrays.asList(
            new TextNode("Item: "),
            new VarNode("item.name")
        );
        
        ListNode listNode = new ListNode("item", "products", body);
        
        assertEquals("item", listNode.getItem());
        assertEquals("products", listNode.getListExpression());
        assertEquals(2, listNode.getBody().size());
        assertTrue(listNode.getBody().get(0) instanceof TextNode);
        assertTrue(listNode.getBody().get(1) instanceof VarNode);
    }

    @Test
    public void testListNodeSerialization() throws Exception {
        List<FtlNode> body = Arrays.asList(new TextNode("Product: "), new VarNode("product"));
        ListNode listNode = new ListNode("product", "productList", body);
        
        // Serialize to JSON
        String json = AstJson.toJson(Collections.singletonList(listNode));
        assertNotNull(json);
        assertTrue(json.contains("\"type\" : \"List\""));
        assertTrue(json.contains("\"item\" : \"product\""));
        assertTrue(json.contains("\"listExpression\" : \"productList\""));
        
        // Deserialize from JSON
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof ListNode);
        
        ListNode deserializedList = (ListNode) deserializedNodes.get(0);
        assertEquals("product", deserializedList.getItem());
        assertEquals("productList", deserializedList.getListExpression());
        assertEquals(2, deserializedList.getBody().size());
    }

    @Test
    public void testListNodeRendering() {
        List<FtlNode> ast = FtlBuilder.create()
                .list("user", "users", b -> b
                    .text("Name: ")
                    .var("user.name")
                    .text("\n"))
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("<#list users as user>"));
        assertTrue(rendered.contains("Name: ${user.name}"));
        assertTrue(rendered.contains("</#list>"));
    }

    @Test
    public void testNestedListNodes() {
        List<FtlNode> ast = FtlBuilder.create()
                .list("category", "categories", b -> b
                    .text("Category: ")
                    .var("category.name")
                    .text("\n")
                    .list("product", "category.products", inner -> inner
                        .text("  Product: ")
                        .var("product.name")
                        .text("\n")))
                .build();

        assertEquals(1, ast.size());
        assertTrue(ast.get(0) instanceof ListNode);
        
        ListNode outerList = (ListNode) ast.get(0);
        assertEquals("category", outerList.getItem());
        assertEquals("categories", outerList.getListExpression());
        assertEquals(4, outerList.getBody().size());
        
        // Check the nested list
        assertTrue(outerList.getBody().get(3) instanceof ListNode);
        ListNode innerList = (ListNode) outerList.getBody().get(3);
        assertEquals("product", innerList.getItem());
        assertEquals("category.products", innerList.getListExpression());
    }

    @Test
    public void testListNodeWithComplexExpression() {
        List<FtlNode> ast = FtlBuilder.create()
                .list("item", "data.items?filter(x -> x.active)", b -> b
                    .text("Active item: ")
                    .var("item.name"))
                .build();

        assertTrue(ast.get(0) instanceof ListNode);
        ListNode listNode = (ListNode) ast.get(0);
        assertEquals("data.items?filter(x -> x.active)", listNode.getListExpression());
    }

    @Test
    public void testListNodeWithEmptyBody() {
        List<FtlNode> emptyBody = Collections.emptyList();
        ListNode listNode = new ListNode("item", "items", emptyBody);
        
        assertEquals("item", listNode.getItem());
        assertEquals("items", listNode.getListExpression());
        assertTrue(listNode.getBody().isEmpty());
    }

    @Test
    public void testListNodeToString() {
        List<FtlNode> body = Arrays.asList(new TextNode("content"));
        ListNode listNode = new ListNode("x", "list", body);
        
        String toString = listNode.toString();
        assertTrue(toString.contains("List"));
        assertTrue(toString.contains("x"));
        assertTrue(toString.contains("list"));
    }

    @Test
    public void testListNodeVisitorPattern() {
        List<FtlNode> body = Arrays.asList(new TextNode("Test"));
        ListNode listNode = new ListNode("test", "testList", body);
        
        // Create a simple visitor to test the accept method
        FtlVisitor visitor = new FtlBaseVisitor() {
            @Override
            public void visit(ListNode node) {
                assertEquals("test", node.getItem());
                assertEquals("testList", node.getListExpression());
            }
        };
        
        // Test that visitor accept works without throwing exceptions
        assertDoesNotThrow(() -> listNode.accept(visitor));
    }

    @Test
    public void testImmutableBody() {
        List<FtlNode> body = Arrays.asList(new TextNode("Item"));
        ListNode listNode = new ListNode("item", "items", body);
        
        // Test that returned body collection is immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            listNode.getBody().add(new TextNode("Should not work"));
        });
    }

    @Test
    public void testListNodeWithIndex() {
        List<FtlNode> ast = FtlBuilder.create()
                .list("item", "items", b -> b
                    .text("Index: ")
                    .var("item_index")
                    .text(", Value: ")
                    .var("item"))
                .build();

        String rendered = new FreeMarkerRenderer().render(ast);
        assertTrue(rendered.contains("Index: ${item_index}"));
        assertTrue(rendered.contains("Value: ${item}"));
    }
}