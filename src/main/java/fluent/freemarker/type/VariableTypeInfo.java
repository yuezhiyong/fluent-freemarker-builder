package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import lombok.Getter;

@Getter
public class VariableTypeInfo {

    private final VarType varType;
    private final String typeName;
    private final boolean shouldStopValidation; // 是否停止后续验证

    public VariableTypeInfo(VarType variableType, String typeName) {
        this(variableType, typeName, false);
    }

    public VariableTypeInfo(VarType variableType, String typeName, boolean shouldStopValidation) {
        this.varType = variableType;
        this.typeName = typeName;
        this.shouldStopValidation = shouldStopValidation;
    }

    public static VariableTypeInfo of(VarType type, String typeName) {
        return new VariableTypeInfo(type, typeName);
    }

    public static VariableTypeInfo stop(VarType type, String typeName) {
        return new VariableTypeInfo(type, typeName, true);
    }
}
