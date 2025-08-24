package fluent.freemarker;

import fluent.freemarker.model.OrderPlus;
import fluent.freemarker.registry.TypeRegistry;
import fluent.freemarker.registry.TypeRegistryFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TypeRegisterTests {

    @Test
    public void testRegisterTypes() {
        TypeRegistry typeRegistry = TypeRegistryFactory.create(TypeRegistryFactory.TypeRegistryType.DEFAULT);
        typeRegistry.register("Order", OrderPlus.class);

        // ✅ 深层路径检查
        assertTrue(typeRegistry.knowsField("Order", "profile"));           // true
        assertTrue(typeRegistry.knowsField("Order", "profile.role"));      // true
        assertTrue(typeRegistry.knowsField("Order", "profile.address.city")); // true

        // ❌ 错误拼写
        assertFalse(typeRegistry.knowsField("Order", "profile.rolex"));     // false

        // 获取建议
        Set<String> suggestions = typeRegistry.getSuggestions("Order", "profile.rolex");
        System.out.println(suggestions);
    }
}
