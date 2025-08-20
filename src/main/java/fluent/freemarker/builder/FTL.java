package fluent.freemarker.builder;

public class FTL {

    public static TemplateBuilder template() {
        return new TemplateBuilder();
    }

    public static TemplateFragment fragment() {
        return new TemplateFragment();
    }
}
