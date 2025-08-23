package fluent.freemarker.validator;

import fluent.freemarker.variable.ValidationRecorder;
import fluent.freemarker.variable.VariableReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalVariableValidator extends AbstractVariableValidator {


    @Override
    protected ValidationResult doValidate(VariableReference reference, ValidationContext context) {
        String rootVar = getRootVariable(reference.getExpression());
        ValidationRecorder recorder = context.getValidationRecorder();
        // 检查是否是作用域变量（避免对作用域变量报错）
        if (recorder != null && recorder.isScopeVariable(rootVar)) {
            // 作用域变量不应该在这里验证，让 ScopeVariableValidator 处理
            return ValidationResult.valid(); // 或者返回 invalid(null) 让下一个处理
        }
        // 检查全局变量
        if (context.getFreemarkerContext().getVariableRegistry().knows(rootVar)) {
            return ValidationResult.valid();
        }
        return ValidationResult.invalid("Variable '" + reference.getExpression() + "' referenced at " + reference.getSource() + " is not defined");
    }


}
