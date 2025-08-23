package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class IncludeNode implements FtlNode {

    public final String template;
    public final Map<String, FtlExpr> params;


    @JsonCreator
    public IncludeNode(@JsonProperty("template") String template, @JsonProperty("params") Map<String, FtlExpr> params) {
        this.template = template;
        this.params = params == null
                ? Collections.<String, FtlExpr>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, FtlExpr>(params));
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Include{" + template + "," + params + "}";
    }
}
