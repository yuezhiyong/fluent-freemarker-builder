package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class MacroCallNode implements FtlNode {

    public final String name; // 宏的名称
    public final Map<String, FtlExpr> args; // 参数名到参数值的映射

    @JsonCreator
    public MacroCallNode(@JsonProperty("name") String name, @JsonProperty("args") Map<String, FtlExpr> args) {
        this.name = name;
        this.args = args == null
                ? Collections.<String, FtlExpr>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(args));
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return "MacroCall{" + "@" + name + "," + args + "}";
    }
}
