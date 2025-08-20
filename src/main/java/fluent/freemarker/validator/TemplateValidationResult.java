package fluent.freemarker.validator;

public class TemplateValidationResult {
    private final boolean valid;
    private final String message;
    private final Exception cause;

    public TemplateValidationResult(boolean valid, String message, Exception cause) {
        this.valid = valid;
        this.message = message;
        this.cause = cause;
    }

    public static TemplateValidationResult valid() {
        return new TemplateValidationResult(true, "OK", null);
    }

    public static TemplateValidationResult invalid(String message, Exception cause) {
        return new TemplateValidationResult(false, message, cause);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Exception getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "TemplateValidationResult{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                '}';
    }
}
