package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

@Slf4j
public class GlobalVariableDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression, ValidationContext context) {
        return context == null || context.getFreemarkerContext() == null || !context.getValidationRecorder().isDefinedGlobally(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression, ValidationContext context) {
        VarKeyType keyType = getGlobalVariableType(variableName, context.getFreemarkerContext());
        return VariableTypeInfo.of(VarType.GLOBAL, keyType.getVarType(), keyType.getVarKey());
    }

    private VarKeyType getGlobalVariableType(String varName, FluentFreemarkerContext context) {
        if (context == null) return VarKeyType.ofKeyType(varName, "object");
        Object value = context.getContext().get(varName);
        if (value != null) {
            return VarKeyType.ofKeyType(varName, inferTypeName(value));
        }
        return VarKeyType.ofKeyType(varName, "object");
    }



    private String inferTypeName(Object value) {
        if (value == null) return "object";

        // 处理集合类型
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            String elementType = getCollectionElementType(collection);
            return "List<" + elementType + ">";
        }

        // 处理数组类型
        if (value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            if (componentType != null) {
                return "Array<" + componentType.getSimpleName() + ">";
            }
            return "Array<object>";
        }

        // 处理 Map 类型
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            String keyType = "object";
            String valueType = "object";

            if (!map.isEmpty()) {
                Map.Entry<?, ?> firstEntry = map.entrySet().iterator().next();
                keyType = firstEntry.getKey() != null ? firstEntry.getKey().getClass().getSimpleName() : "object";
                valueType = firstEntry.getValue() != null ? firstEntry.getValue().getClass().getSimpleName() : "object";
            }
            return "Map<" + keyType + "," + valueType + ">";
        }

        // 其他类型直接返回类名
        return value.getClass().getSimpleName();
    }

    // 获取集合元素类型
    private String getCollectionElementType(Collection<?> collection) {
        if (collection.isEmpty()) {
            return "object";
        }
        // 尝试从集合的第一个元素推断类型
        Object firstElement = collection.iterator().next();
        if (firstElement != null) {
            return firstElement.getClass().getSimpleName();
        }
        return "object";
    }
}
