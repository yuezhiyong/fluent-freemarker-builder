package fluent.freemarker.parser;


import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.RawExpr;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParserChain implements ExpressionParser {
    private final List<ExpressionParser> parsers;

    public ExpressionParserChain() {
        this.parsers = new ArrayList<>();
    }

    public ExpressionParserChain addParser(ExpressionParser parser) {
        parsers.add(parser);
        return this;
    }

    @Override
    public FtlExpr parse(String expression) {
        // 按优先级顺序尝试解析器
        for (ExpressionParser parser : parsers) {
            if (parser.supports(expression)) {
                return parser.parse(expression);
            }
        }
        // 没有解析器能处理，返回默认表达式
        return new RawExpr(expression != null ? expression : "");
    }

    @Override
    public boolean supports(String expression) {
        return parsers.stream().anyMatch(parser -> parser.supports(expression));
    }

    // 创建默认解析器链
    public static ExpressionParserChain createDefaultChain() {
        return new ExpressionParserChain()
                .addParser(new ConditionalExpressionParser())
                .addParser(new BinaryExpressionParser())
                .addParser(new SimpleExpressionParser());
    }
}
