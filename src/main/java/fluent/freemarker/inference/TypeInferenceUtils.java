package fluent.freemarker.inference;

import fluent.freemarker.model.TypeInfo;
import fluent.freemarker.validator.ValidationContext;

public class TypeInferenceUtils {
    private static final GenericTypeInference DEFAULT_INFERENCE = new GenericTypeInference();

    public static TypeInfo inferType(String varName, Object value, ValidationContext context) {
        return DEFAULT_INFERENCE.inferType(varName, value, context);
    }

    public static String inferListItemTypeName(String listVarName, String listTypeName, ValidationContext context) {
        // 优先从类型名称中提取
        String elementType = DEFAULT_INFERENCE.extractElementTypeName(listTypeName);
        if (!"object".equals(elementType)) {
            return elementType;
        }
        // 回退到从名称推断
        return DEFAULT_INFERENCE.inferSingularForm(listVarName);
    }

    public static String extractElementTypeName(String collectionTypeName) {
        return DEFAULT_INFERENCE.extractElementTypeName(collectionTypeName);
    }

    public static String inferSingularForm(String pluralName) {
        return DEFAULT_INFERENCE.inferSingularForm(pluralName);
    }
}
