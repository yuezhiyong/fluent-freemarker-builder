package fluent.freemarker.ast.expr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BinaryExpr implements FtlExpr {

    private final FtlExpr left;

    private final String op;

    private final FtlExpr right;


    @JsonCreator
    public BinaryExpr(@JsonProperty("left") FtlExpr left, @JsonProperty("op") String op, @JsonProperty("right") FtlExpr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }


    @Override
    public String toString() {
        return "Binary{" + left + op + right + "}";
    }
}
