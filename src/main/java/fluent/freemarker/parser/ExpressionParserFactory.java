package fluent.freemarker.parser;

public class ExpressionParserFactory {
    public enum ParserType {
        DEFAULT,
        BINARY,
        SIMPLE,
        CONDITIONAL,
        NO_OP
    }

    public static ExpressionParser create(ParserType type) {
        switch (type) {
            case BINARY:
                return new BinaryExpressionParser();
            case SIMPLE:
                return new SimpleExpressionParser();
            case CONDITIONAL:
                return new ConditionalExpressionParser();
            case NO_OP:
                return new NoOpExpressionParser();
            case DEFAULT:
            default:
                return ExpressionParserChain.createDefaultChain();
        }
    }

    // 创建自定义解析器链
    public static ExpressionParserChain createChain(ExpressionParser... parsers) {
        ExpressionParserChain chain = new ExpressionParserChain();
        for (ExpressionParser parser : parsers) {
            chain.addParser(parser);
        }
        return chain;
    }
}
