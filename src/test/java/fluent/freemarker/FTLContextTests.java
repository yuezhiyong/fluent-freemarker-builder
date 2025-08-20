package fluent.freemarker;

import fluent.freemarker.builder.FTL;
import fluent.freemarker.model.Order;
import fluent.freemarker.model.User;
import fluent.freemarker.variable.FluentFreemarkerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class FTLContextTests {


    @Test
    void testFTLWithContextValidation() throws Exception {
        // 1. 创建上下文并注册变量
        User user = new User("Alice", 30, "a@example.com");
        List<Order> orders = Arrays.asList(new Order("O001"), new Order("O002"));

        FluentFreemarkerContext ctx = FluentFreemarkerContext.create()
                .var("user", user)
                .var("user.name", user.getName())
                .var("orders", orders);

        // 2. 构建模板（绑定 context）
        String template = FTL.template().ctx(ctx)
                .enableValidation(true)
                .append("Hello ").var("user.name")  // ✅ 注册过，通过
                .newline()
                .ifTrue("user.active", ifBlock -> {
                    ifBlock.append("Active");
                })
                .list("orders", "o", "id", listBlock -> {
                    listBlock.append("\t").append("${o.id}");
                })
                //.var("user.namex")  // 如果打开：抛 UnknownVariableException
                .validate()  //  变量 + 语法双重验证
                .toString();

        System.out.println(template);

        // 3. 渲染
        String result = ctx.render(template);

        Assertions.assertTrue(!result.isEmpty());
    }
}
