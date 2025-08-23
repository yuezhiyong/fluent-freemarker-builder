package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

@Getter
public class GlobalNode implements FtlNode {

    public final String var;
    public final FtlExpr expr;

    @JsonCreator
    public GlobalNode(@JsonProperty("var") String var, @JsonProperty("expr") FtlExpr expr) {
        this.var = var;
        this.expr = expr;
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }
}
