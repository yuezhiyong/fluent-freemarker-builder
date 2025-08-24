package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import lombok.extern.slf4j.Slf4j;

// 局部变量检测器
@Slf4j
public class LocalVariableDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression, ValidationContext context) {
        return context == null || context.getValidationRecorder() == null || !context.getValidationRecorder().isDefinedInScope(variableName) || context.getValidationRecorder().isScopeVariable(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression, ValidationContext context) {
        VarKeyType typeName = context.getValidationRecorder().getVariableScopeType(variableName);
        return VariableTypeInfo.of(VarType.LOCAL, typeName.getVarType(), typeName.getVarKey());
    }
}
