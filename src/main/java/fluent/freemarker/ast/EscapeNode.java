package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class EscapeNode implements FtlNode {
    public final String expr;
    public final String asVar;
    public final List<FtlNode> body;

    @JsonCreator
    public EscapeNode(@JsonProperty("expr") String expr, @JsonProperty("asVar") String asVar, @JsonProperty("body") List<FtlNode> body) {
        this.expr = expr;
        this.asVar = asVar;
        this.body = Collections.unmodifiableList(new ArrayList<FtlNode>(body));
    }


    public void accept(FtlVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return "Escape{" + expr + "," + asVar + "," + body + "}";
    }
}
