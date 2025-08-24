package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.exception.ExpressionParseException;

public class ConditionalExpressionParser extends AbstractExpressionParser {
    @Override
    protected boolean doSupports(String expression) {
        return expression != null && (expression.contains(" > ") || expression.contains(" < ") || expression.contains(" >= ") || expression.contains(" <= ") || expression.contains(" == ") || expression.contains(" != ") || expression.contains(" && ") || expression.contains(" || "));
    }

    @Override
    protected FtlExpr doParse(String expression) throws ExpressionParseException {
        // 使用二元表达式解析器来处理
        BinaryExpressionParser binaryParser = new BinaryExpressionParser();
        return binaryParser.parse(expression);
    }
}
