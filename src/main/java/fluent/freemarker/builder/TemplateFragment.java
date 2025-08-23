package fluent.freemarker.builder;

public class TemplateFragment {

    private final StringBuilder content = new StringBuilder();

    public TemplateFragment append(String s) {
        content.append(s);
        return this;
    }

    public TemplateFragment var(String v) {
        return append("${").append(v).append("}");
    }

    public TemplateFragment newline() {
        return append("\n");
    }

    public String render() {
        return content.toString();
    }


}
