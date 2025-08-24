package fluent.freemarker.registrar;

import fluent.freemarker.registry.TypeRegistry;

// 类型注册器接口
public interface TypeRegistrar {

    /**
     * 根据变量值自动注册类型
     */
    void registerTypeIfNeeded(String varName, Object value, TypeRegistry typeRegistry);

}
