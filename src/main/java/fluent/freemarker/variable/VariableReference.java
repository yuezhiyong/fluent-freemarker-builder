package fluent.freemarker.variable;

import lombok.Data;

@Data
public class VariableReference {
    private final String expression;     // 如 "user.name" 或 "o.id"
    private final boolean isLocal;       // 是否是局部变量（如 o）
    private final String localVarType;   // 如果是局部变量，它的类型（如 "Order"）
    private final String source;         // 来源（调试用）

    public VariableReference(String expression, boolean isLocal, String localVarType, String source) {
        this.expression = expression;
        this.isLocal = isLocal;
        this.localVarType = localVarType;
        this.source = source;
    }


    @Override
    public String toString() {
        return "VariableReference{" +
                "expr='" + expression + '\'' +
                ", local=" + isLocal +
                ", type='" + localVarType + '\'' +
                '}';
    }
}
