package fluent.freemarker.type;

import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;

public interface VariableTypeDetector {
    /**
     * 检测变量的类型
     *
     * @param variableName 变量名（根变量名）
     * @param expression   完整表达式
     * @param context      验证上下文
     * @param recorder     验证记录器
     * @return 变量类型信息
     */
    VariableTypeInfo detectType(String variableName, String expression,
                                ValidationContext context, ValidationRecorder recorder);

    /**
     * 是否应该跳过此检测器
     */
    default boolean shouldSkip(String variableName, String expression,
                               ValidationContext context, ValidationRecorder recorder) {
        return false;
    }
}
