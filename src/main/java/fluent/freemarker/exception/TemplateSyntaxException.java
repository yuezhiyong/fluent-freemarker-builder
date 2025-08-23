package fluent.freemarker.exception;

public class TemplateSyntaxException extends RuntimeException {

    public TemplateSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateSyntaxException(Throwable cause) {
        super(cause);
    }

    public TemplateSyntaxException(String message) {
        super(message);
    }
}
