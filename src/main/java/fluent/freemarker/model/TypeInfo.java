package fluent.freemarker.model;

import lombok.Getter;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TypeInfo {
    private final Class<?> clazz;
    private final Map<String, TypeInfo> fields = new HashMap<>();
    private final TypeRegistry registry;

    // 私有构造，通过 TypeRegistry 创建
    public TypeInfo(Class<?> clazz, TypeRegistry registry) {
        this.clazz = clazz;
        this.registry = registry;
        discoverFields();
    }

    private void discoverFields() {
        // === 基础类型、包装类、String、集合等，不再深入 ===
        if (isPrimitiveOrWrapper(clazz) ||
                clazz == String.class ||
                clazz.isArray() ||
                isCollectionOrMap(clazz)) {
            return;
        }

        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);

                // 使用 TypeRegistry 获取 TypeInfo（带缓存）
                TypeInfo fieldTypeInfo = registry.getTypeInfo(field.getType());
                fields.put(field.getName(), fieldTypeInfo);
            }

            // 也可以从 getter 解析
            for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() != 0) continue;
                if (method.getDeclaringClass() == Object.class) continue;

                String name = null;
                Class<?> returnType = method.getReturnType();

                if (method.getName().startsWith("get") && method.getName().length() > 3 && returnType != void.class) {
                    name = Introspector.decapitalize(method.getName().substring(3));
                } else if (method.getName().startsWith("is") && method.getName().length() > 2 &&
                        (returnType == boolean.class || returnType == Boolean.class)) {
                    name = Introspector.decapitalize(method.getName().substring(2));
                }

                if (name != null && !fields.containsKey(name)) {
                    TypeInfo returnTypeInfo = registry.getTypeInfo(returnType);
                    fields.put(name, returnTypeInfo);
                }
            }
        } catch (Exception e) {
            // 防止异常导致崩溃
            throw new RuntimeException("Error discovering fields for " + clazz.getName() + ": " + e.getMessage());
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> c) {
        return c.isPrimitive() ||
                c == Boolean.class || c == Character.class ||
                c == Integer.class || c == Long.class || c == Short.class ||
                c == Byte.class || c == Float.class || c == Double.class ||
                c == Void.class;
    }

    private boolean isCollectionOrMap(Class<?> c) {
        return Collection.class.isAssignableFrom(c) ||
                Map.class.isAssignableFrom(c) ||
                c.isEnum();
    }

    // ====== 业务方法 ======
    public boolean hasFieldPath(String path) {
        if (path == null || path.isEmpty()) return false;
        String[] parts = path.split("[.\\[\\]]");
        TypeInfo current = this;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            current = current.fields.get(part);
            if (current == null) return false;
        }
        return true;
    }

    public Set<String> getSuggestions(String partial) {
        if (partial == null || partial.isEmpty()) return Collections.emptySet();
        String[] parts = partial.split("[.]", 2);
        String first = parts[0];
        if (parts.length == 1) {
            return fields.keySet().stream()
                    .filter(name -> name.startsWith(first) || editDistance(name, first) <= 2)
                    .collect(Collectors.toSet());
        } else {
            TypeInfo nested = fields.get(first);
            return nested != null ? nested.getSuggestions(parts[1]) : fields.keySet().stream()
                    .filter(name -> name.startsWith(first))
                    .collect(Collectors.toSet());
        }
    }

    public Set<String> getAllFields() {
        return new HashSet<>(fields.keySet());
    }

    // 编辑距离
    private int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[i - 1][j - 1]
                        : 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
        return dp[m][n];
    }
}
