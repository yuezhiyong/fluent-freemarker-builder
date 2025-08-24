package fluent.freemarker.validator;

import fluent.freemarker.variable.VariableReference;

public abstract class AbstractVariableValidator implements VariableValidator {

    @Override
    public final ValidationResult validate(VariableReference reference, ValidationContext context) {
        // 检查是否应该跳过验证
        if (skipValidate(reference, context)) {
            return ValidationResult.valid();
        }
        try {
            return doValidate(reference, context);
        } catch (Exception e) {
            return ValidationResult.invalid("Validation error for '" + reference.getExpression() + "': " + e.getMessage());
        }
    }

    @Override
    public boolean skipValidate(VariableReference reference, ValidationContext context) {
        // 如果上下文为空，跳过验证
        return context == null || context.getFreemarkerContext() == null;
    }


    /**
     * 获取表达式的根变量
     * @param expression 表达式
     * @return 根变量
     */
    protected String getRootVariable(String expression) {
        if (expression == null || expression.isEmpty()) return "";
        int dotIndex = expression.indexOf('.');
        return dotIndex > 0 ? expression.substring(0, dotIndex) : expression;
    }

    /**
     * 真正的业务校验方法
     *
     * @param reference 变量引用的信息
     * @param context   校验上下文
     * @return 校验结果
     */
    protected abstract ValidationResult doValidate(VariableReference reference, ValidationContext context);
}
