package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class VarNode implements FtlNode {

    public final String expression;


    @JsonCreator
    public VarNode(@JsonProperty("expression") String expression) {
        this.expression = expression;
    }

    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Var{" + expression + "}";
    }
}
