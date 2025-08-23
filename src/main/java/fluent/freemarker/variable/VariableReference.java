package fluent.freemarker.variable;

import fluent.freemarker.model.VarType;
import lombok.Getter;

@Getter
public class VariableReference {
    private final String expression;     // 如 "user.name" 或 "o.id"
    private final VarType varType;   // 如果是局部变量，它的类型（如 "Order"）
    private final String varTypeName;
    private final String source;         // 来源（调试用）

    public VariableReference(String expression, VarType varType, String varTypeName, String source) {
        this.expression = expression;
        this.varType = varType;
        this.varTypeName = varTypeName;
        this.source = source;
    }

    @Override
    public String toString() {
        return "VariableReference{" + "expression='" + expression + '\'' + ", varType=" + varType + ", varTypeName='" + varTypeName + '\'' + ", source='" + source + '\'' + '}';
    }
}
