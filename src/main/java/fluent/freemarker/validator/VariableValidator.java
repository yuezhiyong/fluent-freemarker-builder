package fluent.freemarker.validator;

import fluent.freemarker.variable.VariableReference;

public interface VariableValidator {

    ValidationResult validate(VariableReference reference, ValidationContext context);

    // 是否跳过验证
    default boolean skipValidate(VariableReference reference, ValidationContext context) {
        return false;
    }
}
