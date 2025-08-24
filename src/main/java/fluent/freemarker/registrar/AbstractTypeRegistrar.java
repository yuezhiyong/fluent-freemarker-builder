package fluent.freemarker.registrar;

import fluent.freemarker.registry.TypeRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

@Slf4j
public abstract class AbstractTypeRegistrar implements TypeRegistrar {
    @Override
    public final void registerTypeIfNeeded(String varName, Object value, TypeRegistry typeRegistry) {
        if (value == null || typeRegistry == null) {
            return;
        }

        try {
            if (shouldSkipRegistration(varName, value, typeRegistry)) {
                return;
            }
            doRegisterType(varName, value, typeRegistry);
        } catch (Exception e) {
            log.error("Failed to register type for variable '{}': {}", varName, e.getMessage());
        }
    }

    protected boolean shouldSkipRegistration(String varName, Object value, TypeRegistry typeRegistry) {
        return false;
    }

    protected abstract void doRegisterType(String varName, Object value, TypeRegistry typeRegistry);

    /**
     * 检查是否是基础类型或包装类型（受保护的方法，供子类使用）
     */
    protected boolean isPrimitiveOrWrapper(Class<?> c) {
        return c.isPrimitive() || c == Boolean.class || c == Character.class || c == Integer.class || c == Long.class || c == Short.class || c == Byte.class || c == Float.class || c == Double.class || c == Void.class;
    }

    /**
     * 注册对象类型（受保护的方法，供子类使用）
     */
    protected void registerObjectType(Object obj, TypeRegistry typeRegistry) {
        if (obj == null || typeRegistry == null) return;

        Class<?> clazz = obj.getClass();

        // 避免重复注册基础类型
        if (isPrimitiveOrWrapper(clazz) || clazz == String.class) {
            return;
        }
        try {
            typeRegistry.getTypeInfo(clazz);
        } catch (Exception e) {
            log.error("Failed to register type '{}': {}", clazz.getSimpleName(), e.getMessage());
        }
    }

    /**
     * 注册集合元素类型（受保护的方法，供子类使用）
     */
    protected void registerCollectionElementTypes(Collection<?> collection, TypeRegistry typeRegistry) {
        if (collection == null || collection.isEmpty() || typeRegistry == null) {
            return;
        }

        try {
            Object firstElement = collection.iterator().next();
            if (firstElement != null) {
                registerObjectType(firstElement, typeRegistry);
            }
        } catch (Exception e) {
            log.error("Failed to register collection element types: {}", e.getMessage());
        }
    }

    /**
     * 注册数组元素类型（受保护的方法，供子类使用）
     */
    protected void registerArrayElementTypes(Object array, TypeRegistry typeRegistry) {
        if (array == null || !array.getClass().isArray() || typeRegistry == null) {
            return;
        }

        try {
            Object[] objArray = (Object[]) array;
            if (objArray.length > 0 && objArray[0] != null) {
                registerObjectType(objArray[0], typeRegistry);
            }
        } catch (Exception e) {
            log.debug("Failed to register array element types: {}", e.getMessage());
        }
    }

    /**
     * 注册映射值类型（受保护的方法，供子类使用）
     */
    protected void registerMapValueTypes(Map<?, ?> map, TypeRegistry typeRegistry) {
        if (map == null || map.isEmpty() || typeRegistry == null) {
            return;
        }

        try {
            Object firstValue = map.values().iterator().next();
            if (firstValue != null) {
                registerObjectType(firstValue, typeRegistry);
            }
        } catch (Exception e) {
            log.error("Failed to register map value types: {}", e.getMessage());
        }
    }
}
