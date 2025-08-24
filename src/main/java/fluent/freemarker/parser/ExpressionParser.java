package fluent.freemarker.parser;

import fluent.freemarker.ast.expr.FtlExpr;

// 表达式解析器接口
public interface ExpressionParser {
    /**
     * 将字符串表达式解析为表达式树
     */
    FtlExpr parse(String expression);

    /**
     * 检查是否支持解析给定的表达式
     */
    boolean supports(String expression);
}
