package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ScopeVariableMarker;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

// 作用域项变量检测器
@Slf4j
public class ScopeItemDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression,
                              ValidationContext context, ValidationRecorder recorder) {
        return recorder == null || !recorder.isScopeVariable(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression,
                                            ValidationContext context, ValidationRecorder recorder) {
        Object value = recorder.getAssignedValue(variableName);
        String typeName = "object";
        if (value instanceof ScopeVariableMarker) {
            typeName = ((ScopeVariableMarker) value).getType();
        } else if (value != null) {
            typeName = value.getClass().getSimpleName();
        }
        // 作用域变量验证通过后停止后续验证
        return VariableTypeInfo.stop(VarType.SCOPE_ITEM, typeName);
    }
}
