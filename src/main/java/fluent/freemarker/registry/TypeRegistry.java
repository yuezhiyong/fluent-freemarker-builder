package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;

import java.util.Map;
import java.util.Set;

public interface TypeRegistry {

    /**
     * 获取类的类型信息
     */
    TypeInfo getTypeInfo(Class<?> clazz);

    /**
     * 检查类型是否知道指定字段路径
     */
    boolean knowsField(String typeName, String fieldPath);

    /**
     * 获取字段建议
     */
    Set<String> getSuggestions(String typeName, String partialField);

    /**
     * 注册类型
     */
    <T> TypeRegistry register(String typeName, Class<T> clazz);

    /**
     * 获取所有已知类型信息
     */
    Map<Class<?>, TypeInfo> getAllTypeInfos();

    /**
     * 清空注册表
     */
    void clear();

    /**
     * 检查是否包含指定类型
     */
    boolean containsType(Class<?> clazz);
}
