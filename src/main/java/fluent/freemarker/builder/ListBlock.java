package fluent.freemarker.builder;

import fluent.freemarker.exception.UnknownVariableException;

import java.util.Set;
import java.util.function.Consumer;

public class ListBlock {
    private final TemplateBuilder parent;
    private final String itemVar;
    private final String itemType;

    public ListBlock(TemplateBuilder parent, String collection, String itemVar, String itemType) {
        this.parent = parent;
        this.itemVar = itemVar;
        this.itemType = itemType;
        // 开始 list
        parent.appendRaw("<#list ").appendRaw(collection)
                .appendRaw(" as ").appendRaw(itemVar).appendRaw(">\n");
    }

    public ListBlock append(String text) {
        parent.append(text);
        return this;
    }


    public ListBlock var(String varPath) {
        // 拼接 ${itemVar.varPath}
        parent.appendRaw("${").appendRaw(itemVar).appendRaw(".").appendRaw(varPath).appendRaw("}");
        // 验证字段合法性
        if (parent.isValidationEnabled() && itemType != null) {
            if (!parent.getTypeRegistry().knowsField(itemType, varPath)) {
                Set<String> suggestions = parent.getTypeRegistry().getSuggestions(itemType, varPath);
                throw new UnknownVariableException(
                        "Field '" + varPath + "' not found in type '" + itemType +
                                "'. Did you mean one of? " + suggestions);
            }
        }
        return this;
    }


    public ListBlock newline() {
        return append("\n");
    }

    public ListBlock ifTrue(String expr, Consumer<ConditionalBlock> body) {
        parent.ifTrue(expr, body);
        return this;
    }

    public ListBlock ifNotNull(String expr, Consumer<ConditionalBlock> body) {
        parent.ifNotNull(expr, body);
        return this;
    }

    public ListBlock list(String collection, String item, String itemType, Consumer<ListBlock> body) {
        parent.list(collection, item, itemType, body);
        return this;
    }


    public TemplateBuilder endList(){
        parent.appendRaw("\n</#list>\n");
        return parent;
    }
}
