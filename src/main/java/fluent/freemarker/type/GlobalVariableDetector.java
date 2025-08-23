package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalVariableDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression,
                              ValidationContext context, ValidationRecorder recorder) {
        return context == null ||
                context.getFreemarkerContext() == null ||
                !recorder.isDefinedGlobally(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression,
                                            ValidationContext context, ValidationRecorder recorder) {
        String typeName = getGlobalVariableType(variableName, context.getFreemarkerContext());
        return VariableTypeInfo.of(VarType.GLOBAL, typeName);
    }

    private String getGlobalVariableType(String varName, FluentFreemarkerContext context) {
        if (context == null) return "object";
        Object value = context.getContext().get(varName);
        if (value != null) {
            return value.getClass().getSimpleName();
        }
        return "object";
    }
}
