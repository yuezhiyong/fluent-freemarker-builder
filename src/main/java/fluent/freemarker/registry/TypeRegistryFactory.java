package fluent.freemarker.registry;

public class TypeRegistryFactory {
    public enum TypeRegistryType {
        DEFAULT, CONCURRENT, EXPIRING, READ_ONLY
    }

    public static TypeRegistry create(TypeRegistryType type) {
        return create(type, 0);
    }

    public static TypeRegistry create(TypeRegistryType type, long expirationTimeMillis) {
        switch (type) {
            case CONCURRENT:
                return new ConcurrentTypeRegistry();
            case EXPIRING:
                return new ExpiringTypeRegistry(expirationTimeMillis);
            case READ_ONLY:
                return new ReadOnlyTypeRegistry(new DefaultTypeRegistry());
            case DEFAULT:
            default:
                return new DefaultTypeRegistry();
        }
    }

    // 创建包装的只读注册表
    public static TypeRegistry createReadOnly(TypeRegistry delegate) {
        return new ReadOnlyTypeRegistry(delegate);
    }
}
