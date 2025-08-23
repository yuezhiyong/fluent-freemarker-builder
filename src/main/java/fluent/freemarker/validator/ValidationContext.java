package fluent.freemarker.validator;

import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.ValidationRecorder;
import lombok.Getter;

@Getter
public class ValidationContext {
    private final FluentFreemarkerContext freemarkerContext;
    private final ValidationRecorder validationRecorder;

    public ValidationContext(FluentFreemarkerContext freemarkerContext, ValidationRecorder validationRecorder) {
        this.freemarkerContext = freemarkerContext;
        this.validationRecorder = validationRecorder;
    }
}
