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

    // 可以缓存常用片段
    public static TemplateFragment userCard() {
        return FTL.fragment()
                .append("<div class='user'>")
                .append("Name: ").var("user.name").newline()
                .append("Email: ").var("user.email")
                .append("</div>");
    }
}
