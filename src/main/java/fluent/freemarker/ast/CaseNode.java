package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;

import java.util.List;

public class CaseNode implements FtlNode {

    public final List<FtlExpr> values;
    public final List<FtlNode> body;

    @JsonCreator
    public CaseNode(@JsonProperty("values") List<FtlExpr> values, @JsonProperty("body") List<FtlNode> body) {
        this.values = values;
        this.body = body;
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Case{" + values + "," + body + "}";
    }
}
