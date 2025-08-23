package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

import java.util.List;

@Getter
public class SwitchNode implements FtlNode {

    public final FtlExpr expr;
    public final List<CaseNode> cases;
    public final List<FtlNode> defaultBody;

    @JsonCreator
    public SwitchNode(@JsonProperty("expr") FtlExpr expr, @JsonProperty("cases") List<CaseNode> cases, @JsonProperty("defaultBody") List<FtlNode> defaultBody) {
        this.expr = expr;
        this.cases = cases;
        this.defaultBody = defaultBody;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Switch{" + expr + "," + cases + "," + defaultBody + "}";
    }
}
