package fluent.freemarker.exception;

public class UnknownVariableException extends RuntimeException {

    public UnknownVariableException(String message, Throwable cause) {
        super(message, cause);
    }


    public UnknownVariableException(String message) {
        super(message);
    }
}
