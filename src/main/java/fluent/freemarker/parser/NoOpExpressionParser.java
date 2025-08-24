package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.RawExpr;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpExpressionParser implements ExpressionParser {
    @Override
    public FtlExpr parse(String expression) {
        return new RawExpr(expression != null ? expression : "");
    }

    @Override
    public boolean supports(String expression) {
        return true; // 支持所有表达式
    }
}
