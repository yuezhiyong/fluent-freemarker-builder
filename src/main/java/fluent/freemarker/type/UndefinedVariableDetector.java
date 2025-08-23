package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UndefinedVariableDetector extends AbstractVariableTypeDetector {

    @Override
    protected VariableTypeInfo doDetectType(String variableName, String expression,
                                            ValidationContext context, ValidationRecorder recorder) {
        // 如果前面的检测器都没匹配到，就是未定义变量
        return VariableTypeInfo.stop(VarType.UNDEFINED, "undefined");
    }
}
