package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.*;
import fluent.freemarker.exception.ExpressionParseException;

public class BinaryExpressionParser extends AbstractExpressionParser {


    private static final String[][] OPERATOR_PRECEDENCE = {
            {"||"},                    // 逻辑或 - 最低优先级
            {"&&"},                    // 逻辑与
            {">=", "<="},             // 比较操作符
            {">", "<"},               // 比较操作符
            {"==", "!="},             // 相等操作符
            {"+", "-"},               // 加减法
            {"*", "/", "%"}           // 乘除法 - 最高优先级
    };

    @Override
    protected boolean doSupports(String expression) {
        if (expression == null || expression.isEmpty()) return false;

        // 检查是否包含任何支持的操作符
        for (String[] opGroup : OPERATOR_PRECEDENCE) {
            for (String op : opGroup) {
                if (expression.contains(op)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected FtlExpr doParse(String expression) throws ExpressionParseException {
        return parseWithPrecedence(expression, 0);
    }

    /**
     * 按操作符优先级解析表达式
     */
    private FtlExpr parseWithPrecedence(String expression, int precedenceLevel)
            throws ExpressionParseException {
        if (precedenceLevel >= OPERATOR_PRECEDENCE.length) {
            // 最高优先级，解析简单表达式
            return parseSimpleExpression(expression);
        }

        String[] operators = OPERATOR_PRECEDENCE[precedenceLevel];

        // 从右到左查找操作符（处理左结合性）
        for (int i = operators.length - 1; i >= 0; i--) {
            String op = operators[i];
            int index = findOperatorIndex(expression, op);

            if (index > 0) {
                String leftPart = expression.substring(0, index).trim();
                String rightPart = expression.substring(index + op.length()).trim();

                FtlExpr left = parseWithPrecedence(leftPart, precedenceLevel);
                FtlExpr right = parseWithPrecedence(rightPart, precedenceLevel + 1);
                return new BinaryExpr(left, op, right);
            }
        }
        // 没有找到当前优先级的操作符，尝试下一级
        return parseWithPrecedence(expression, precedenceLevel + 1);
    }

    /**
     * 解析简单表达式
     */
    private FtlExpr parseSimpleExpression(String expression) throws ExpressionParseException {
        if (expression == null || expression.isEmpty()) {
            return new LiteralExpr(null);
        }

        expression = expression.trim();

        // 处理括号
        if (expression.startsWith("(") && expression.endsWith(")")) {
            String inner = expression.substring(1, expression.length() - 1).trim();
            return parse(inner); // 递归解析
        }

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

        // 未知表达式
        return new RawExpr(expression);
    }

    /**
     * 查找操作符索引（考虑括号）
     */
    private int findOperatorIndex(String expression, String operator) {
        int parenCount = 0;
        for (int i = 0; i < expression.length() - operator.length() + 1; i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                parenCount++;
            } else if (c == ')') {
                parenCount--;
            } else if (parenCount == 0) {
                // 检查是否匹配操作符
                if (i + operator.length() <= expression.length() &&
                        expression.substring(i, i + operator.length()).equals(operator)) {
                    return i;
                }
            }
        }
        return -1;
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
                long value = Long.parseLong(str);
                // 如果在 int 范围内，返回 Integer
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    return (int) value;
                }
                return value;
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
            if ((str.startsWith("'") && str.endsWith("'")) ||
                    (str.startsWith("\"") && str.endsWith("\""))) {
                return str.substring(1, str.length() - 1);
            }
        }
        return str;
    }

    private boolean isValidVariableReference(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("^[a-zA-Z_][a-zA-Z0-9_.]*$");
    }
}
