package fluent.freemarker.type;

import fluent.freemarker.model.VarType;
import lombok.Getter;

@Getter
public class VariableTypeInfo {

    private final VarType varType;
    private final String typeName;
    private final String variableKey;        // 变量属性key（如 "user", "orders"）

    public VariableTypeInfo(VarType variableType, String typeName, String variableKey) {
        this.varType = variableType;
        this.typeName = typeName;
        this.variableKey = variableKey;
    }

    public VariableTypeInfo(VarType variableType, String typeName) {
        this(variableType, typeName, null);
    }

    public static VariableTypeInfo of(VarType type, String typeName, String variableKey) {
        return new VariableTypeInfo(type, typeName, variableKey);
    }

    public static VariableTypeInfo of(VarType type, String typeName) {
        return new VariableTypeInfo(type, typeName, null);
    }

    // 便捷方法
    public boolean hasVariableKey() {
        return variableKey != null && !variableKey.isEmpty();
    }

    @Override
    public String toString() {
        return "VariableTypeInfo{" +
                "varType=" + varType +
                ", typeName='" + typeName + '\'' +
                ", variableKey='" + variableKey + '\'' +
                '}';
    }
}
