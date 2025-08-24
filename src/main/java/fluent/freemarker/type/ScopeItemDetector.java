package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ScopeVariableMarker;
import lombok.extern.slf4j.Slf4j;


// 作用域项变量检测器
@Slf4j
public class ScopeItemDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression, ValidationContext context) {
        return context == null || context.getValidationRecorder() == null || !context.getValidationRecorder().isScopeVariable(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression, ValidationContext context) {
        Object value = context.getValidationRecorder().getValue(variableName);
        String typeName = "object";
        if (value instanceof ScopeVariableMarker) {
            typeName = ((ScopeVariableMarker) value).getType();
        } else if (value != null) {
            typeName = value.getClass().getSimpleName();
        }
        // 返回变量类型信息，包含变量key
        return VariableTypeInfo.of(VarType.SCOPE_ITEM, typeName, variableName);
    }

}
