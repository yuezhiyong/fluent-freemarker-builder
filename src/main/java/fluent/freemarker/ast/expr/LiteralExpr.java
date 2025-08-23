package fluent.freemarker.ast.expr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LiteralExpr implements FtlExpr {

    private final Object value;

    @JsonCreator
    public LiteralExpr(@JsonProperty("value") Object value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "Literal{"+value.toString() + "}";
    }
}
