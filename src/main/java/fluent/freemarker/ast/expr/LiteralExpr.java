package fluent.freemarker.ast.expr;

import lombok.Getter;

@Getter
public class LiteralExpr implements FtlExpr {

    private final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value.toString();
    }
}
