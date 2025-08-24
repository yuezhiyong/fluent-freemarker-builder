package fluent.freemarker.inference;

import fluent.freemarker.model.TypeInfo;
import fluent.freemarker.registry.TypeRegistry;
import fluent.freemarker.validator.ValidationContext;

import java.util.*;

public class GenericTypeInference implements VariableTypeInference, TypeNameExtractor {
    private final Map<String, String> irregularPlurals = new HashMap<>();
    private final List<PluralRule> pluralRules = new ArrayList<>();

    public GenericTypeInference() {
        initializeDefaultRules();
    }

    // VariableTypeInference 接口实现
    @Override
    public TypeInfo inferType(String varName, Object value, ValidationContext context) {
        if (value == null) {
            return createDefaultTypeInfo(context);
        }

        TypeRegistry typeRegistry = getTypeRegistry(context);
        if (typeRegistry == null) return null;

        // 处理不同类型的值
        if (value instanceof Collection) {
            return inferCollectionType((Collection<?>) value, typeRegistry);
        } else if (value.getClass().isArray()) {
            return inferArrayType(value, typeRegistry);
        } else if (value instanceof Map) {
            return inferMapType((Map<?, ?>) value, typeRegistry);
        } else {
            return typeRegistry.getTypeInfo(value.getClass());
        }
    }

    @Override
    public TypeInfo inferElementType(TypeInfo collectionTypeInfo, ValidationContext context) {
        // 根据你的 TypeInfo 结构实现元素类型推断
        // 这里可以访问 collectionTypeInfo 的字段信息
        return collectionTypeInfo;
    }

    @Override
    public String inferTypeNameFromName(String varName) {
        return inferSingularForm(varName);
    }

    // TypeNameExtractor 接口实现
    @Override
    public String extractElementTypeName(String collectionTypeName) {
        if (collectionTypeName == null || collectionTypeName.isEmpty()) {
            return "object";
        }

        // 处理泛型格式：List<TestOrder> -> TestOrder
        if (collectionTypeName.contains("<") && collectionTypeName.contains(">")) {
            int start = collectionTypeName.indexOf('<');
            int end = collectionTypeName.indexOf('>');
            if (start > 0 && end > start) {
                return collectionTypeName.substring(start + 1, end);
            }
        }

        return "object";
    }

    @Override
    public String inferSingularForm(String pluralName) {
        if (pluralName == null || pluralName.isEmpty()) {
            return pluralName;
        }

        // 检查不规则映射
        String singular = irregularPlurals.get(pluralName.toLowerCase());
        if (singular != null) {
            return preserveCase(pluralName, singular);
        }

        // 应用规则映射
        for (PluralRule rule : pluralRules) {
            if (pluralName.toLowerCase().matches(".*" + rule.getPluralPattern())) {
                return pluralName.replaceAll("(?i)" + rule.getPluralPattern() + "$", rule.getSingularReplacement());
            }
        }

        return pluralName;
    }

    @Override
    public void addPluralRule(String pluralPattern, String singularReplacement) {
        pluralRules.add(0, new PluralRule(pluralPattern, singularReplacement));
    }

    // 私有辅助方法
    private TypeInfo createDefaultTypeInfo(ValidationContext context) {
        TypeRegistry typeRegistry = getTypeRegistry(context);
        return typeRegistry != null ? typeRegistry.getTypeInfo(Object.class) : null;
    }

    private TypeRegistry getTypeRegistry(ValidationContext context) {
        return context != null && context.getFreemarkerContext() != null ?
                context.getFreemarkerContext().getTypeRegistry() : null;
    }

    private TypeInfo inferCollectionType(Collection<?> collection, TypeRegistry typeRegistry) {
        if (collection.isEmpty()) {
            return typeRegistry.getTypeInfo(Collection.class);
        }

        Object firstElement = collection.iterator().next();
        return firstElement != null ?
                typeRegistry.getTypeInfo(firstElement.getClass()) :
                typeRegistry.getTypeInfo(Object.class);
    }

    private TypeInfo inferArrayType(Object array, TypeRegistry typeRegistry) {
        Class<?> componentType = array.getClass().getComponentType();
        return componentType != null ?
                typeRegistry.getTypeInfo(componentType) :
                typeRegistry.getTypeInfo(Object[].class);
    }

    private TypeInfo inferMapType(Map<?, ?> map, TypeRegistry typeRegistry) {
        if (map.isEmpty()) {
            return typeRegistry.getTypeInfo(Map.class);
        }

        Object valueType = map.values().iterator().next();
        return valueType != null ?
                typeRegistry.getTypeInfo(valueType.getClass()) :
                typeRegistry.getTypeInfo(Object.class);
    }

    private String preserveCase(String original, String replacement) {
        if (original.equals(original.toUpperCase())) {
            return replacement.toUpperCase();
        } else if (Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
        }
        return replacement;
    }

    private void initializeDefaultRules() {
        // 初始化不规则单复数映射
        irregularPlurals.put("children", "child");
        irregularPlurals.put("men", "man");
        irregularPlurals.put("women", "woman");
        irregularPlurals.put("people", "person");
        irregularPlurals.put("feet", "foot");
        irregularPlurals.put("teeth", "tooth");

        // 初始化后缀规则
        pluralRules.add(new PluralRule("ies$", "y"));
        pluralRules.add(new PluralRule("ves$", "f"));
        pluralRules.add(new PluralRule("oes$", "o"));
        pluralRules.add(new PluralRule("xes$", "x"));
        pluralRules.add(new PluralRule("zes$", "z"));
        pluralRules.add(new PluralRule("ches$", "ch"));
        pluralRules.add(new PluralRule("shes$", "sh"));
        pluralRules.add(new PluralRule("ses$", "s"));
        pluralRules.add(new PluralRule("s$", ""));
    }

    // 内部类
    private static class PluralRule {
        private final String pluralPattern;
        private final String singularReplacement;

        public PluralRule(String pluralPattern, String singularReplacement) {
            this.pluralPattern = pluralPattern;
            this.singularReplacement = singularReplacement;
        }

        public String getPluralPattern() { return pluralPattern; }
        public String getSingularReplacement() { return singularReplacement; }
    }
}
