package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

@Getter
public class ReturnNode implements FtlNode {

    private final FtlExpr expr;

    @JsonCreator
    public ReturnNode(@JsonProperty("expr") FtlExpr expr) {
        this.expr = expr;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Return{" + expr + "}";
    }
}
