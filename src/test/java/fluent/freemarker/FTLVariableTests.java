package fluent.freemarker;

import fluent.freemarker.builder.FTL;
import fluent.freemarker.model.Profile;
import fluent.freemarker.model.User;
import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.TemplateVariableChecker;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FTLVariableTests {

    @Test
    public void testVariableRegistry() {
        User user = new User("Alice", 30, "a@example.com");
        Profile profile = new Profile("Engineer", "IT");

        FluentFreemarkerContext ctx = FluentFreemarkerContext.create()
                .var("user", user)
                .var("user.name", user.getName())  // 显式注册子路径（可选）
                .var("user.profile", profile)
                .var("user.profile.role", profile.getRole())
                .var("orders[0].id", "O001");

        // 检查模板

        String template = FTL.template().append("Hello").var("user.name")
                .append(",").append("Role").var("user.profile.role")
                .append("ID").append(",").append("orders[0].id").validate().toString();

        TemplateVariableChecker checker = new TemplateVariableChecker(ctx.getVariableRegistry());
        List<String> unknown = checker.findUnknownVariables(template);

        assertTrue(unknown.isEmpty());

        // 测试错误路径
        String badTemplate = "Name: ${user.namex}, Role: ${profile.role}";
        List<String> missing = checker.findUnknownVariables(badTemplate);
        assertEquals(Arrays.asList("user.namex", "profile.role"), missing);

        // 获取建议
        List<String> suggestions = checker.getSuggestions("user.name");
        assertTrue(suggestions.contains("user.name"));
    }
}
