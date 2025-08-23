package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

@Getter
public class LocalNode implements FtlNode {

    public final String var;
    public final FtlExpr expr;

    @JsonCreator
    public LocalNode(@JsonProperty("var") String var, @JsonProperty("expr") FtlExpr expr) {
        this.var = var;
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
        return "Local{" + var + ", " + expr + "}";
    }
}
