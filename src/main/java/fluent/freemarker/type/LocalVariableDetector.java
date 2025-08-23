package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

// 局部变量检测器
@Slf4j
public class LocalVariableDetector extends AbstractVariableTypeDetector {
    @Override
    public boolean shouldSkip(String variableName, String expression, ValidationContext context, ValidationRecorder recorder) {
        return recorder == null ||
                !recorder.isDefinedInScope(variableName) ||
                recorder.isScopeVariable(variableName);
    }

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression,
                                            ValidationContext context, ValidationRecorder recorder) {
        String typeName = recorder.getScopeType(variableName);
        return VariableTypeInfo.of(VarType.LOCAL, typeName != null ? typeName : "object");
    }
}
