package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ConcurrentTypeRegistry extends AbstractTypeRegistry {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public TypeInfo getTypeInfo(Class<?> clazz) {
        if (clazz == null) return null;

        // 读锁获取缓存
        lock.readLock().lock();
        try {
            TypeInfo cached = classToTypeInfo.get(clazz);
            if (cached != null) {
                return cached;
            }
        } finally {
            lock.readLock().unlock();
        }

        // 写锁创建新的 TypeInfo
        lock.writeLock().lock();
        try {
            // 双重检查
            TypeInfo cached = classToTypeInfo.get(clazz);
            if (cached != null) {
                return cached;
            }
            return createAndCacheTypeInfo(clazz);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    protected TypeInfo createAndCacheTypeInfo(Class<?> clazz) {
        TypeInfo info = new TypeInfo(clazz, this);
        classToTypeInfo.put(clazz, info);
        typeInfoMap.put(clazz.getSimpleName(), info);
        return info;
    }

    @Override
    public Map<Class<?>, TypeInfo> getAllTypeInfos() {
        lock.readLock().lock();
        try {
            return new HashMap<>(classToTypeInfo);
        } finally {
            lock.readLock().unlock();
        }
    }
}
