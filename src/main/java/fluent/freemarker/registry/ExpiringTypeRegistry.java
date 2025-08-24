package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpiringTypeRegistry extends AbstractTypeRegistry {

    private final Map<Class<?>, Long> typeTimestamps = new ConcurrentHashMap<>();
    private final long expirationTimeMillis;

    public ExpiringTypeRegistry(long expirationTimeMillis) {
        this.expirationTimeMillis = expirationTimeMillis;
    }

    @Override
    public TypeInfo getTypeInfo(Class<?> clazz) {
        if (clazz == null) return null;
        // 检查是否过期
        Long timestamp = typeTimestamps.get(clazz);
        if (timestamp != null && System.currentTimeMillis() - timestamp > expirationTimeMillis) {
            // 过期，移除旧的
            classToTypeInfo.remove(clazz);
            String simpleName = clazz.getSimpleName();
            if (simpleName != null) {
                typeInfoMap.remove(simpleName);
            }
            typeTimestamps.remove(clazz);
        }
        return super.getTypeInfo(clazz);
    }

    @Override
    protected TypeInfo createAndCacheTypeInfo(Class<?> clazz) {
        TypeInfo info = new TypeInfo(clazz, this);
        classToTypeInfo.put(clazz, info);
        typeInfoMap.put(clazz.getSimpleName(), info);
        typeTimestamps.put(clazz, System.currentTimeMillis());
        return info;
    }

}
