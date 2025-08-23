package fluent.freemarker.ast.expr;

import lombok.Getter;

@Getter
public final class RawExpr implements FtlExpr {

    private final String code;

    public RawExpr(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
