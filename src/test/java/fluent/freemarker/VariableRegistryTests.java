package fluent.freemarker;

import fluent.freemarker.variable.VariablePath;
import fluent.freemarker.variable.VariableRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class VariableRegistryTests {

    @Test
    public void testVariableRegistry() {
        VariableRegistry registry = new VariableRegistry();
        registry.register("user.name");
        registry.register("user.email");
        registry.register("app.title");

        // 存在性
        Assertions.assertTrue(registry.knows("user.name"));
        Assertions.assertFalse(registry.knows("user.age"));

        // 建议
        List<VariablePath> suggestions = registry.findSuggestions("user.namex");
        List<String> variableStr = suggestions.stream().map(VariablePath::toString).collect(Collectors.toList());
        Assertions.assertTrue(variableStr.contains("user.name"));

        // 去重
        registry.register("user.name");
        Assertions.assertTrue(registry.getAllPaths().size() == 3);

        // 根变量
        Assertions.assertTrue(registry.isRootRegistered("user"));
        Assertions.assertFalse(registry.isRootRegistered("profile"));
    }
}
