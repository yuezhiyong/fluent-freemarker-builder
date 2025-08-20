package fluent.freemarker.exception;

public class TemplateSyntaxException extends RuntimeException{
    public TemplateSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
