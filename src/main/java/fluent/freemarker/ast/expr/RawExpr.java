package fluent.freemarker.ast.expr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class RawExpr implements FtlExpr {

    private final String code;

    @JsonCreator
    public RawExpr(@JsonProperty("code") String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Raw{" + code + "}";
    }
}
