package fluent.freemarker.builder;

import fluent.freemarker.utils.FTLUtils;

import java.util.function.Consumer;

public class ConditionalBlock {

    private final TemplateBuilder parent;

    public ConditionalBlock(TemplateBuilder parent) {
        this.parent = parent;
    }

    // ====== 基本文本操作 ======
    public ConditionalBlock append(String text) {
        parent.appendRaw(text);
        return this;
    }

    public ConditionalBlock var(String varName) {
        return append("${").append(varName).append("}");
    }

    public ConditionalBlock newline() {
        parent.appendRaw("\n");
        return this;
    }

    public ConditionalBlock indent(int spaces) {
        return append(FTLUtils.spaces(spaces)); // 或使用内部 repeat
    }


    // ====== 嵌套结构：if ======
    public ConditionalBlock ifTrue(String expr) {
        parent.appendRaw("<#if ").appendRaw(expr).appendRaw(">\n");
        return new ConditionalBlock(parent);
    }

    public ConditionalBlock ifNotNull(String expr) {
        parent.appendRaw("<#if ").appendRaw(expr).appendRaw("??>\n");
        return new ConditionalBlock(parent);
    }

    public ConditionalBlock ifNotNull(String expr, Consumer<ConditionalBlock> body) {
        parent.ifNotNull(expr, body);
        return this;
    }

    // ====== 结束当前 if 块 ======
    public TemplateBuilder endIf() {
        parent.appendRaw("</#if>\n");
        return parent;
    }

    // ====== else / elseif ======
    public ConditionalBlock elseIf(String expr) {
        parent.appendRaw("<#elseif ").appendRaw(expr).appendRaw(">\n");
        return this;
    }

    public ConditionalBlock elseBlock() {
        parent.appendRaw("<#else>\n");
        return this;
    }


    // 支持嵌套 if（函数式）
    public ConditionalBlock ifTrue(String expr, Consumer<ConditionalBlock> body) {
        parent.ifTrue(expr, body);
        return this;
    }

    // 支持嵌套 list
    public ConditionalBlock list(String collection, String item, String itemType, Consumer<ListBlock> body) {
        parent.list(collection, item, itemType, body);
        return this;
    }


}
