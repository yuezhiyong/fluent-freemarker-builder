package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;

public class DefaultTypeRegistry extends AbstractTypeRegistry {
    @Override
    protected TypeInfo createAndCacheTypeInfo(Class<?> clazz) {
        // 创建新的 TypeInfo（会递归调用 getTypeInfo）
        TypeInfo info = new TypeInfo(clazz, this);
        classToTypeInfo.put(clazz, info);
        typeInfoMap.put(clazz.getSimpleName(), info);
        return info;
    }

}
