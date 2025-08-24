package fluent.freemarker.exception;

public class ExpressionParseException extends RuntimeException {


    public ExpressionParseException(String message, Throwable cause) {
        super(message, cause);
    }


    public ExpressionParseException(String message) {
        super(message);
    }
}
