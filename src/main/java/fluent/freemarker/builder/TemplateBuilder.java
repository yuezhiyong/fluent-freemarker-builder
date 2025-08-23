package fluent.freemarker.builder;

import fluent.freemarker.exception.TemplateSyntaxException;
import fluent.freemarker.exception.UnknownVariableException;
import fluent.freemarker.model.TypeRegistry;
import fluent.freemarker.utils.FTLUtils;
import fluent.freemarker.validator.FTLValidator;
import fluent.freemarker.validator.TemplateValidationResult;
import fluent.freemarker.variable.*;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class TemplateBuilder {

    private final StringBuilder content = new StringBuilder();
    private VariableRegistry variableRegistry;
    private TypeRegistry typeRegistry;
    private final ValidationRecorder recorder = new ValidationRecorder();
    private boolean validationEnabled = true;

    // 构造函数：必须传入 FluentContext（从中提取 registry）
    public TemplateBuilder(FluentFreemarkerContext context) {
        this.variableRegistry = context.getVariableRegistry();
        this.typeRegistry = context.getTypeRegistry();
    }

    // 无 context → 不验证
    public TemplateBuilder() {
        this.variableRegistry = null;
        this.validationEnabled = false;
    }


    public TemplateBuilder ctx(FluentFreemarkerContext context) {
        this.variableRegistry = context.getVariableRegistry();
        this.validationEnabled = true;
        return this;
    }


    public TemplateBuilder enableValidation(boolean enableValidation) {
        this.validationEnabled = enableValidation;
        return this;
    }


    // ====== var 方法：记录引用 ======
    public TemplateBuilder var(String varName) {
        content.append("${").append(varName).append("}");
        int dotIndex = varName.indexOf('.');
        if (dotIndex > 0) {
            String root = varName.substring(0, dotIndex);
            String path = varName.substring(dotIndex + 1);
            if (recorder.isInScope(path)) {
                // 局部变量字段
                String typeName = recorder.getScopeType(root);
                recorder.record(new VariableReference(varName, true, typeName, "list-item"));
            } else {
                // 全局变量
                if (!variableRegistry.knows(varName)) {
                    throw new UnknownVariableException("Unknown variable: " + varName);
                }
                recorder.record(new VariableReference(varName, false, null, "global"));
            }
        } else {
            // 无点：如 ${name}
            if (!variableRegistry.knows(varName)) {
                throw new UnknownVariableException("Unknown variable: " + varName);
            }
            recorder.record(new VariableReference(varName, false, null, "global"));
        }
        return this;
    }

    // ====== validate()：基于 recorder 检查 ======
    public TemplateBuilder validate() {
        String template = toString();
        // 1. 语法验证
        TemplateValidationResult syntaxResult = FTLValidator.validate(template);
        if (!syntaxResult.isValid()) {
            throw new TemplateSyntaxException("Syntax error: " + syntaxResult.getMessage(), syntaxResult.getCause());
        }
        if (!validationEnabled) {
            return this;
        }
        // 2. 结构化变量验证
        for (VariableReference ref : recorder.getReferences()) {
            if (ref.isLocal()) {
                if (!typeRegistry.knowsField(ref.getLocalVarType(), ref.getExpression().substring(ref.getExpression().indexOf('.') + 1))) {
                    Set<String> suggestions = typeRegistry.getSuggestions(ref.getLocalVarType(), ref.getExpression());
                    throw new UnknownVariableException(
                            "Field '" + ref.getExpression() + "' not found in type '" + ref.getLocalVarType() +
                                    "'. Did you mean? " + suggestions);
                }
            } else {
                if (!variableRegistry.knows(ref.getExpression())) {
                    List<VariablePath> suggestions = variableRegistry.findSuggestions(ref.getExpression());
                    throw new UnknownVariableException(
                            "Variable '" + ref.getExpression() + "' is not registered." +
                                    (suggestions.isEmpty() ? "" : " Did you mean: " + suggestions));
                }
            }
        }
        return this;
    }


    public TemplateBuilder append(String text) {
        content.append(text);
        return this;
    }

    public TemplateBuilder newline() {
        return append("\n");
    }

    public TemplateBuilder indent(int spaces) {
        return append(FTLUtils.spaces(spaces));
    }


    // <#if condition>
    public ConditionalBlock ifNotNull(String expr) {
        content.append("<#if ").append(expr).append("??>\n");
        return new ConditionalBlock(this);
    }

    public TemplateBuilder ifNotNull(String expr, Consumer<ConditionalBlock> body) {
        content.append("<#if ").append(expr).append("??>\n");
        ConditionalBlock block = new ConditionalBlock(this);
        body.accept(block);
        content.append("</#if>\n");
        return this;
    }

    public ConditionalBlock ifTrue(String expr) {
        content.append("<#if ").append(expr).append(">\n");
        return new ConditionalBlock(this);
    }

    // ====== list 方法：管理作用域 ======
    public TemplateBuilder list(String collection, String itemVar, String itemType, Consumer<ListBlock> body) {
        // 进入作用域
        recorder.pushScope(itemVar, itemType);
        ListBlock listBlock = new ListBlock(this, collection, itemVar, itemType);
        body.accept(listBlock);
        // 退出作用域
        recorder.popScope();
        return listBlock.endList();
    }


    @Override
    public String toString() {
        return content.toString();
    }

    // 内部使用：允许 block 添加内容
    TemplateBuilder appendRaw(String text) {
        content.append(text);
        return this;
    }


    // ====== 函数式 if ======
    public TemplateBuilder ifTrue(String expr, Consumer<ConditionalBlock> body) {
        content.append("<#if ").append(expr).append(">\n");
        ConditionalBlock block = new ConditionalBlock(this);
        body.accept(block); // 执行 Lambda
        content.append("\n</#if>\n"); // 自动结束
        return this;
    }


    // ====== else / elseif 支持（可选）======
    public TemplateBuilder ifElse(Consumer<ConditionalBlock> ifBody, Consumer<ConditionalBlock> elseBody) {
        return ifElse(null, ifBody, elseBody);
    }

    public TemplateBuilder ifElse(String expr, Consumer<ConditionalBlock> ifBody, Consumer<ConditionalBlock> elseBody) {
        String condition = expr != null ? expr : "true";
        content.append("<#if ").append(condition).append(">\n");
        ConditionalBlock ifBlock = new ConditionalBlock(this);
        ifBody.accept(ifBlock);
        content.append("<#else>\n");
        ConditionalBlock elseBlock = new ConditionalBlock(this);
        elseBody.accept(elseBlock);
        content.append("</#if>\n");
        return this;
    }


}
