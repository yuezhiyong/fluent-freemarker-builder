package fluent.freemarker.ast.expr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class IdentifierExpr implements FtlExpr {

    private final String name;

    @JsonCreator
    public IdentifierExpr(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Identifier{" + name + "}";
    }
}
