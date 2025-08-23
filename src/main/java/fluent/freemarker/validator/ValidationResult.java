package fluent.freemarker.validator;

import lombok.Getter;

@Getter
public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;
    private final boolean shouldContinue; // 是否继续后续验证

    private ValidationResult(boolean valid, String errorMessage, boolean shouldContinue) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.shouldContinue = shouldContinue;
    }


    public static ValidationResult valid() {
        return new ValidationResult(true, null, true);
    }

    public static ValidationResult invalid(String errorMessage) {
        return new ValidationResult(false, errorMessage, true);
    }

    public static ValidationResult invalidInterrupted(String errorMessage) {
        return new ValidationResult(false, errorMessage, false);
    }
}
