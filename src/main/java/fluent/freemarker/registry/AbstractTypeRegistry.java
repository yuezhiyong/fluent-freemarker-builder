package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTypeRegistry implements TypeRegistry {

    protected final Map<String, TypeInfo> typeInfoMap = new ConcurrentHashMap<>();
    protected final Map<Class<?>, TypeInfo> classToTypeInfo = new ConcurrentHashMap<>();

    @Override
    public TypeInfo getTypeInfo(Class<?> clazz) {
        if (clazz == null) return null;

        // 优先从缓存获取
        TypeInfo cached = classToTypeInfo.get(clazz);
        if (cached != null) {
            return cached;
        }

        // 创建新的 TypeInfo
        return createAndCacheTypeInfo(clazz);
    }

    @Override
    public boolean knowsField(String typeName, String fieldPath) {
        TypeInfo root = typeInfoMap.get(typeName);
        return root != null && root.hasFieldPath(fieldPath);
    }

    @Override
    public Set<String> getSuggestions(String typeName, String partialField) {
        TypeInfo root = typeInfoMap.get(typeName);
        if (root == null) return Collections.emptySet();
        return root.getSuggestions(partialField);
    }

    @Override
    public <T> TypeRegistry register(String typeName, Class<T> clazz) {
        if (typeName != null && clazz != null) {
            TypeInfo info = getTypeInfo(clazz);
            typeInfoMap.put(typeName, info);
        }
        return this;
    }

    @Override
    public Map<Class<?>, TypeInfo> getAllTypeInfos() {
        return new HashMap<>(classToTypeInfo);
    }

    @Override
    public void clear() {
        typeInfoMap.clear();
        classToTypeInfo.clear();
    }

    @Override
    public boolean containsType(Class<?> clazz) {
        return classToTypeInfo.containsKey(clazz);
    }

    /**
     * 创建并缓存 TypeInfo
     */
    protected abstract TypeInfo createAndCacheTypeInfo(Class<?> clazz);
}
