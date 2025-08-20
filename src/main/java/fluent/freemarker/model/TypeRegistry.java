package fluent.freemarker.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeRegistry {
    private final Map<String, TypeInfo> typeInfoMap = new HashMap<>();
    private final Map<Class<?>, TypeInfo> classToTypeInfo = new HashMap<>();

    public <T> TypeRegistry register(String typeName, Class<T> clazz) {
        TypeInfo info = getTypeInfo(clazz);
        typeInfoMap.put(typeName, info);
        return this;
    }

    public boolean knowsField(String typeName, String fieldPath) {
        TypeInfo root = typeInfoMap.get(typeName);
        return root != null && root.hasFieldPath(fieldPath);
    }

    public Set<String> getSuggestions(String typeName, String partialField) {
        TypeInfo root = typeInfoMap.get(typeName);
        if (root == null) return Collections.emptySet();
        return root.getSuggestions(partialField);
    }

    // ====== TypeInfo 工厂方法 ======
    public TypeInfo getTypeInfo(Class<?> clazz) {
        if (clazz == null) return null;

        // 优先从缓存获取
        if (classToTypeInfo.containsKey(clazz)) {
            return classToTypeInfo.get(clazz);
        }

        // 创建新 TypeInfo（会递归调用 getTypeInfo）
        TypeInfo info = new TypeInfo(clazz, this);
        classToTypeInfo.put(clazz, info);
        return info;
    }

    // 获取所有已知类型（调试用）
    public Map<Class<?>, TypeInfo> getAllTypeInfos() {
        return Collections.unmodifiableMap(classToTypeInfo);
    }

}
