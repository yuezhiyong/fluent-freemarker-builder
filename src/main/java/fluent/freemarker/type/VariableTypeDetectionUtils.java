package fluent.freemarker.type;

import fluent.freemarker.utils.PathUtils;
import fluent.freemarker.validator.ValidationContext;

public class VariableTypeDetectionUtils {
    private static final VariableTypeDetectionChain DEFAULT_CHAIN = VariableTypeDetectionChain.createDefaultChain();

    public static VariableTypeInfo detectVariableType(String variableName, String expression, ValidationContext context) {
        return DEFAULT_CHAIN.detectType(variableName, expression, context);
    }

    public static VariableTypeInfo detectVariableType(String expression, ValidationContext context) {
        String rootVar = PathUtils.getRootVariable(expression);
        return detectVariableType(rootVar, expression, context);
    }
}
