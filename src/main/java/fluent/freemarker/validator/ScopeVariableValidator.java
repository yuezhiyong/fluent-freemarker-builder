package fluent.freemarker.validator;

import fluent.freemarker.variable.ScopeVariable;
import fluent.freemarker.variable.ValidationRecorder;
import fluent.freemarker.variable.VariableReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScopeVariableValidator extends AbstractVariableValidator {

    @Override
    public boolean skipValidate(VariableReference reference, ValidationContext context) {
        if (super.skipValidate(reference, context)) {
            return true;
        }
        ValidationRecorder recorder = context.getValidationRecorder();
        String rootVar = getRootVariable(reference.getExpression());
        return recorder.getGlobalAssignedVars().contains(rootVar);
    }

    @Override
    protected ValidationResult doValidate(VariableReference reference, ValidationContext context) {
        String rootVar = getRootVariable(reference.getExpression());
        ValidationRecorder recorder = context.getValidationRecorder();
        // 检查是否是作用域变量（即使作用域已结束，但变量在引用时是有效的）
        if (recorder.isInScope(rootVar)) {
            return ValidationResult.valid();
        }
        // 检查是否在当前或父作用域中被赋值
        if (recorder.isAssigned(rootVar)) {
            Object value = recorder.getAssignedValue(rootVar);
            if (isScopeVariable(value)) {
                return ValidationResult.valid();
            }
        }
        // 不是作用域变量，让下一个验证器处理
        return ValidationResult.invalid(null); // null 表示让下一个验证器决定
    }

    private boolean isScopeVariable(Object value) {
        if (value == null) return false;
        return value instanceof ScopeVariable;
    }


}
