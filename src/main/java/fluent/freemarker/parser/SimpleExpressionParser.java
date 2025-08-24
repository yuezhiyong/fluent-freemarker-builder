package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.IdentifierExpr;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.ast.expr.RawExpr;
import fluent.freemarker.exception.ExpressionParseException;

public class SimpleExpressionParser extends AbstractExpressionParser{

    @Override
    protected boolean doSupports(String expression) {
        return expression != null && !expression.contains(" ") && !expression.contains("\t");
    }

    @Override
    protected FtlExpr doParse(String expression) throws ExpressionParseException {
        if (expression == null || expression.isEmpty()) {
            return new LiteralExpr(null);
        }

        expression = expression.trim();

        // 处理字面量
        if (isNumber(expression)) {
            return new LiteralExpr(parseNumber(expression));
        }

        if (isStringLiteral(expression)) {
            return new LiteralExpr(unquoteString(expression));
        }

        if ("true".equalsIgnoreCase(expression) || "false".equalsIgnoreCase(expression)) {
            return new LiteralExpr(Boolean.parseBoolean(expression));
        }

        // 处理变量引用
        if (isValidVariableReference(expression)) {
            return new IdentifierExpr(expression);
        }

        // 默认作为原始表达式
        return new RawExpr(expression);
    }

    private boolean isNumber(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Object parseNumber(String str) {
        try {
            if (str.contains(".")) {
                return Double.parseDouble(str);
            } else {
                return Long.parseLong(str);
            }
        } catch (NumberFormatException e) {
            return str;
        }
    }

    private boolean isStringLiteral(String str) {
        return str.length() >= 2 &&
                ((str.startsWith("'") && str.endsWith("'")) ||
                        (str.startsWith("\"") && str.endsWith("\"")));
    }

    private String unquoteString(String str) {
        if (str.length() >= 2) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private boolean isValidVariableReference(String str) {
        if (str == null || str.isEmpty()) return false;
        // 允许点号分隔的变量名
        return str.matches("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$");
    }
}
