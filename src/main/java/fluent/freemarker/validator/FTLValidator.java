package fluent.freemarker.validator;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringReader;

import static freemarker.template.Configuration.VERSION_2_3_32;


public class FTLValidator {
    private static final Configuration VALIDATION_CONFIG;

    static {
        VALIDATION_CONFIG = new Configuration(VERSION_2_3_32);
        VALIDATION_CONFIG.setClassLoaderForTemplateLoading(
                FTLValidator.class.getClassLoader(), "/");
        VALIDATION_CONFIG.setDefaultEncoding("UTF-8");
        // 不加载真实文件，仅解析字符串
        VALIDATION_CONFIG.setTemplateLoader(new freemarker.cache.StringTemplateLoader());
    }

    /**
     * Validates the given template string.
     */
    public static TemplateValidationResult validate(String templateContent) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            return TemplateValidationResult.invalid("Template is null or empty", null);
        }

        try {
            new Template("validation-template", new StringReader(templateContent), VALIDATION_CONFIG);
            return TemplateValidationResult.valid();
        } catch (IOException e) {
            return TemplateValidationResult.invalid(e.getMessage(), e);
        }
    }
}
