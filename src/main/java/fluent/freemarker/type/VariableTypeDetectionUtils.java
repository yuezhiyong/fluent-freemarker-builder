package fluent.freemarker.type;

import fluent.freemarker.utils.PathUtils;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;

public class VariableTypeDetectionUtils {
    private static final VariableTypeDetectionChain DEFAULT_CHAIN =
            VariableTypeDetectionChain.createDefaultChain();

    public static VariableTypeInfo detectVariableType(String variableName, String expression,
                                                      ValidationContext context, ValidationRecorder recorder) {
        return DEFAULT_CHAIN.detectType(variableName, expression, context, recorder);
    }

    public static VariableTypeInfo detectVariableType(String expression,
                                                      ValidationContext context, ValidationRecorder recorder) {
        String rootVar = PathUtils.getRootVariable(expression);
        return detectVariableType(rootVar, expression, context, recorder);
    }
}
