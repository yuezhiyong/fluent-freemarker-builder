package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class VisitNode implements FtlNode {
    public final FtlExpr nodeExpr;
    public final Map<String, FtlExpr> args;

    @JsonCreator
    public VisitNode(@JsonProperty("expr") FtlExpr nodeExpr, @JsonProperty("args") Map<String, FtlExpr> args) {
        this.nodeExpr = nodeExpr;
        this.args = args == null
                ? Collections.<String, FtlExpr>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, FtlExpr>(args));
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }
}
