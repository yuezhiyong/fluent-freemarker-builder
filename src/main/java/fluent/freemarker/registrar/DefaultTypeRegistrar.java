package fluent.freemarker.registrar;

import fluent.freemarker.registry.TypeRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

@Slf4j
public class DefaultTypeRegistrar extends AbstractTypeRegistrar{
    @Override
    protected boolean shouldSkipRegistration(String varName, Object value, TypeRegistry typeRegistry) {
        // 跳过 null 值和基础类型
        return value == null || isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class;
    }

    @Override
    protected void doRegisterType(String varName, Object value, TypeRegistry typeRegistry) {
        // 注册对象本身的类型
        registerObjectType(value, typeRegistry);

        // 根据值的类型注册相关类型
        if (value instanceof Collection) {
            registerCollectionElementTypes((Collection<?>) value, typeRegistry);
        } else if (value.getClass().isArray()) {
            registerArrayElementTypes(value, typeRegistry);
        } else if (value instanceof Map) {
            registerMapValueTypes((Map<?, ?>) value, typeRegistry);
        }
    }
}
