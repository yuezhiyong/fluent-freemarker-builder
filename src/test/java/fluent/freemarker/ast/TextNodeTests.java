package fluent.freemarker.ast;

import fluent.freemarker.builder.AstJson;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextNodeTests {

    @Test
    public void testTextNode() throws Exception {
        FtlNode textNode = new TextNode("Hello, world");
        // 序列化
        String json = AstJson.toJson(Collections.singletonList(textNode));

        // 反序列化
        List<FtlNode> deserializedNodes = AstJson.fromJson(json);
        assertEquals(1, deserializedNodes.size());
        assertTrue(deserializedNodes.get(0) instanceof TextNode);
        assertEquals("Hello, world", ((TextNode) deserializedNodes.get(0)).getText());

        // 渲染
        String rendered = new FreeMarkerRenderer().render(Collections.singletonList(textNode));
        assertEquals("Hello, world", rendered);
    }


    @Test
    public void testAstJson() throws Exception {
        // 1. 构建 AST
        List<FtlNode> ast = FtlBuilder.create()
                .text("Hello ")
                .var("name")
                .ifBlock("flag", b -> b.text("show"))
                .build();

        // 2. AST → JSON
        String json = AstJson.toJson(ast);
        System.out.println(json);
    }
}
