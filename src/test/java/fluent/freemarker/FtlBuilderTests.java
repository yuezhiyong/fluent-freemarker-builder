package fluent.freemarker;

import fluent.freemarker.ast.FtlNode;
import fluent.freemarker.builder.FreeMarkerRenderer;
import fluent.freemarker.builder.FtlBuilder;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

public class FtlBuilderTests {
    @Test
    void testBuilder() {
        List<FtlNode> ast = FtlBuilder.create()
                .text("User list:")
                .list("user", "users", b -> b
                        .ifElseBlock("user.age > 18",
                                then -> then.text("Adult: ").var("user.name"),
                                else_ -> else_.text("Minor: ").var("user.name")
                        )
                )
                .macro("greet", new LinkedHashMap<String, String>() {{
                    put("name", "");
                }}, b -> b.text("Hello, ").var("name").text("!"))
                .build();

        String ftl = new FreeMarkerRenderer().render(ast);

        System.out.println(ftl);
    }
}
