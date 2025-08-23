package fluent.freemarker.ast.expr;

import lombok.Getter;

@Getter
public class IdentifierExpr implements FtlExpr {

    private final String name;

    public IdentifierExpr(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
