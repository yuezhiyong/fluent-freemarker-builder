package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.RawExpr;
import fluent.freemarker.exception.ExpressionParseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractExpressionParser implements ExpressionParser {
    @Override
    public final FtlExpr parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return createDefaultExpression(expression);
        }

        try {
            if (shouldSkipParsing(expression)) {
                return createDefaultExpression(expression);
            }
            return doParse(expression);
        } catch (Exception e) {
            log.debug("Failed to parse expression '{}': {}", expression, e.getMessage());
            return createFallbackExpression(expression, e);
        }
    }

    @Override
    public boolean supports(String expression) {
        return expression != null && doSupports(expression);
    }

    protected boolean shouldSkipParsing(String expression) {
        return false;
    }

    protected abstract boolean doSupports(String expression);

    protected abstract FtlExpr doParse(String expression) throws ExpressionParseException;

    protected FtlExpr createDefaultExpression(String expression) {
        return new RawExpr(expression != null ? expression : "");
    }

    protected FtlExpr createFallbackExpression(String expression, Exception error) {
        return new RawExpr(expression != null ? expression : "");
    }
}
