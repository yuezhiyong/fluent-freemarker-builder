package fluent.freemarker.ast.expr;

import lombok.Getter;

@Getter
public class BinaryExpr implements FtlExpr {

    private final FtlExpr left;

    private final String op;

    private final FtlExpr right;


    public BinaryExpr(FtlExpr left, String op, FtlExpr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }


}
