package fluent.freemarker.registry;

import fluent.freemarker.model.TypeInfo;

import java.util.Map;
import java.util.Set;

public class ReadOnlyTypeRegistry implements TypeRegistry {

    private final TypeRegistry delegate;

    public ReadOnlyTypeRegistry(TypeRegistry delegate) {
        this.delegate = delegate;
    }

    @Override
    public TypeInfo getTypeInfo(Class<?> clazz) {
        return delegate.getTypeInfo(clazz);
    }

    @Override
    public boolean knowsField(String typeName, String fieldPath) {
        return delegate.knowsField(typeName, fieldPath);
    }

    @Override
    public Set<String> getSuggestions(String typeName, String partialField) {
        return delegate.getSuggestions(typeName, partialField);
    }

    @Override
    public <T> TypeRegistry register(String typeName, Class<T> clazz) {
        throw new UnsupportedOperationException("ReadOnlyTypeRegistry does not support registration");
    }

    @Override
    public Map<Class<?>, TypeInfo> getAllTypeInfos() {
        return delegate.getAllTypeInfos();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ReadOnlyTypeRegistry does not support clearing");
    }

    @Override
    public boolean containsType(Class<?> clazz) {
        return delegate.containsType(clazz);
    }
}
