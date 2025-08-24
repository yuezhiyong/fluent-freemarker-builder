package fluent.freemarker.validator;

import fluent.freemarker.model.VarType;
import fluent.freemarker.variable.VariableReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeValidator extends AbstractVariableValidator {

    // 跳过验证的条件：
    // 1. 上下文为空
    // 2. 没有类型注册表
    // 3. 不是需要类型检查的变量类型
    // 4. 表达式不包含字段访问（没有点号）
    @Override
    public boolean skipValidate(VariableReference reference, ValidationContext context) {

        if (context == null || context.getFreemarkerContext() == null) {
            return true;
        }

        if (context.getFreemarkerContext().getTypeRegistry() == null) {
            return true;
        }

        // 只对局部变量和作用域项变量进行类型检查
        VarType varType = reference.getVarType();
        if (varType != VarType.LOCAL && varType != VarType.SCOPE_ITEM) {
            return true;
        }

        // 如果表达式不包含字段访问，跳过类型检查
        if (!reference.getExpression().contains(".")) {
            return true;
        }

        // 如果类型名称为空或未定义，跳过检查
        return reference.getVarTypeName() == null || reference.getVarTypeName().equals("undefined") || reference.getVarTypeName().equals("object");
    }

    @Override
    protected ValidationResult doValidate(VariableReference reference, ValidationContext context) {
        try {
            String expression = reference.getExpression();
            String typeName = reference.getVarTypeName();
            if (expression.contains(".")) {
                String fieldPath = expression.substring(expression.indexOf('.') + 1);
                if (!context.getFreemarkerContext().getTypeRegistry().knowsField(typeName, fieldPath)) {
                    return ValidationResult.invalid("Field '" + fieldPath + "' may not exist on type '" + typeName + "' for variable '" + expression + "'");
                }
            }
            return ValidationResult.valid();
        } catch (Exception e) {
            return ValidationResult.invalid("Type validation failed for '" + reference.getExpression() + "': " + e.getMessage());
        }
    }
}
