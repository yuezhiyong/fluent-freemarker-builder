package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AssignNode implements FtlNode {

    private final String varName;

    private final String valueExpr;


    @JsonCreator
    public AssignNode(@JsonProperty("varName") String varName, @JsonProperty("valueExpr") String valueExpr) {
        this.varName = varName;
        this.valueExpr = valueExpr;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Assign{" + varName + "=" + valueExpr + "}";
    }
}
