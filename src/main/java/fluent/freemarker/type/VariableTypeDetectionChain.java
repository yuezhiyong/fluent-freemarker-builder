package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.variable.ValidationRecorder;

import java.util.ArrayList;
import java.util.List;

public class VariableTypeDetectionChain {
    private final List<VariableTypeDetector> detectors;

    public VariableTypeDetectionChain() {
        this.detectors = new ArrayList<>();
    }

    public VariableTypeDetectionChain addDetector(VariableTypeDetector detector) {
        detectors.add(detector);
        return this;
    }

    public VariableTypeInfo detectType(String variableName, String expression,
                                       ValidationContext context, ValidationRecorder recorder) {
        for (VariableTypeDetector detector : detectors) {
            VariableTypeInfo typeInfo = detector.detectType(variableName, expression, context, recorder);
            if (typeInfo != null) {
                return typeInfo;
            }
        }
        return VariableTypeInfo.of(VarType.UNDEFINED, "undefined");
    }

    // 创建默认检测器链
    public static VariableTypeDetectionChain createDefaultChain() {
        return new VariableTypeDetectionChain()
                .addDetector(new ScopeItemDetector())
                .addDetector(new LocalVariableDetector())
                .addDetector(new GlobalVariableDetector())
                .addDetector(new UndefinedVariableDetector());
    }
}
