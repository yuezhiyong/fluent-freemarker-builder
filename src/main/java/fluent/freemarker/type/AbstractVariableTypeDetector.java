package fluent.freemarker.type;

import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractVariableTypeDetector implements VariableTypeDetector{

    @Override
    public final VariableTypeInfo detectType(String variableName, String expression,
                                             ValidationContext context, ValidationRecorder recorder) {
        if (shouldSkip(variableName, expression, context, recorder)) {
            return null; // 让下一个检测器处理
        }
        return doDetectType(variableName, expression, context, recorder);
    }

    protected abstract VariableTypeInfo doDetectType(String variableName, String expression,
                                                     ValidationContext context, ValidationRecorder recorder);
}
