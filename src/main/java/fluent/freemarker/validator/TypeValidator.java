package fluent.freemarker.validator;

import fluent.freemarker.variable.VariableReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeValidator extends AbstractVariableValidator {

    @Override
    public boolean skipValidate(VariableReference reference, ValidationContext context) {
        // 如果不是局部变量或没有类型信息，跳过验证
        return context == null ||
                context.getFreemarkerContext() == null ||
                !reference.isLocal() ||
                reference.getLocalVarType() == null;
    }

    @Override
    protected ValidationResult doValidate(VariableReference reference, ValidationContext context) {
        try {
            String expression = reference.getExpression();
            String type = reference.getLocalVarType();
            if (expression.contains(".")) {
                String fieldPath = expression.substring(expression.indexOf('.') + 1);
                if (!context.getFreemarkerContext().getTypeRegistry().knowsField(type, fieldPath)) {
                    return ValidationResult.invalid("Field '" + fieldPath + "' may not exist on type '" + type + "' for variable '" + expression + "'");
                }
            }
            return ValidationResult.valid();
        } catch (Exception e) {
            return ValidationResult.invalid("Type validation failed for '" + reference.getExpression() + "': " + e.getMessage());
        }
    }
}
