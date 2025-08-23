package fluent.freemarker.builder;

import fluent.freemarker.ast.*;
import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.IdentifierExpr;
import fluent.freemarker.ast.expr.LiteralExpr;
import fluent.freemarker.validator.ValidationContext;
import fluent.freemarker.validator.VariableValidationChain;
import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.variable.ScopeVariableMarker;
import fluent.freemarker.variable.ValidationRecorder;
import fluent.freemarker.variable.VariableReference;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FtlBuilder {
    private final List<FtlNode> nodes = new ArrayList<>();
    @Getter
    private final FluentFreemarkerContext context;
    @Getter
    private final ValidationRecorder validationRecorder;
    private final boolean isRootBuilder;
    private String currentSourceLocation;


    // 私有构造函数
    private FtlBuilder(FluentFreemarkerContext context, ValidationRecorder validationRecorder, boolean isRootBuilder) {
        this.context = context;
        this.validationRecorder = validationRecorder;
        this.isRootBuilder = isRootBuilder;
        this.currentSourceLocation = context != null ? context.getCurrentSourceLocation() : "unknown";
        initValidationRecorder();
    }


    private void initValidationRecorder() {
        if (validationRecorder != null && context != null) {
            validationRecorder.wrapCtx(context);
        }
    }

    // 无参数构造函数 - 保持向后兼容性
    public static FtlBuilder create() {
        return new FtlBuilder(null, new ValidationRecorder(), true);
    }

    // 带上下文的构造函数
    public static FtlBuilder create(FluentFreemarkerContext context) {
        if (context == null) {
            return create(); // 如果上下文为null，使用无参数版本
        }
        return new FtlBuilder(context, new ValidationRecorder(), true);
    }

    // 获取验证器链
    private VariableValidationChain getValidationChain() {
        if (context != null && context.getVariableValidationChain() != null) {
            return context.getVariableValidationChain();
        }
        // 如果上下文没有提供验证器链，使用默认的
        return VariableValidationChain.createDefaultChain();
    }

    // 内部使用 - 创建子构建器
    private static FtlBuilder createChild(FluentFreemarkerContext context, ValidationRecorder validationRecorder) {
        FtlBuilder child = new FtlBuilder(context, validationRecorder, false);
        return child;
    }


    // 设置当前源位置（用于调试信息）
    public FtlBuilder at(String location) {
        this.currentSourceLocation = location;
        return this;
    }

    // 获取当前位置信息
    private String getCurrentLocation() {
        return currentSourceLocation + ":line:" + (nodes.size() + 1);
    }

    // 辅助方法：获取表达式的根变量名
    private String getRootVariable(String expression) {
        if (expression == null || expression.isEmpty()) return "";
        int dotIndex = expression.indexOf('.');
        return dotIndex > 0 ? expression.substring(0, dotIndex) : expression;
    }

    // ==================== 构建方法 ====================

    public FtlBuilder text(String text) {
        nodes.add(new TextNode(text));
        return this;
    }

    public FtlBuilder var(String name) {
        // 只有在有上下文时才记录变量引用
        if (context != null && validationRecorder != null) {
            String rootVar = getRootVariable(name);
            // 判断是否是局部变量（在当前作用域中）
            boolean isLocal = validationRecorder.isInScope(rootVar);
            String localVarType = isLocal ? validationRecorder.getScopeType(rootVar) : null;
            // 记录变量引用用于后续验证
            VariableReference ref = new VariableReference(name, isLocal, localVarType, getCurrentLocation());
            validationRecorder.record(ref);
        }
        nodes.add(new VarNode(name));
        return this;
    }

    public FtlBuilder assign(String varName, String valueExpr) {
        // 只有在有上下文时才记录变量赋值
        if (context != null && validationRecorder != null) {
            // 记录变量赋值
            validationRecorder.assign(varName, valueExpr);
        }
        nodes.add(new AssignNode(varName, valueExpr));
        return this;
    }

    public FtlBuilder ifBlock(String condition, Consumer<FtlBuilder> thenBody) {
        return ifElseBlock(condition, thenBody, null);
    }

    public FtlBuilder ifElseBlock(String condition, Consumer<FtlBuilder> thenBody, Consumer<FtlBuilder> elseBody) {
        // 只有在有上下文时才记录条件变量引用
        if (context != null && validationRecorder != null) {
            // 记录条件变量引用
            boolean isLocal = validationRecorder.isInScope(getRootVariable(condition));
            String localVarType = isLocal ? validationRecorder.getScopeType(getRootVariable(condition)) : null;
            VariableReference ref = new VariableReference(condition, isLocal, localVarType, getCurrentLocation());
            validationRecorder.record(ref);
        }

        FtlBuilder thenBuilder = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        thenBody.accept(thenBuilder);

        FtlBuilder elseBuilder = null;
        if (elseBody != null) {
            elseBuilder = context != null ?
                    createChild(context, validationRecorder) :
                    createChildWithoutContext(validationRecorder);
            elseBody.accept(elseBuilder);
        }

        nodes.add(new IfNode(condition, thenBuilder.build(),
                elseBuilder != null ? elseBuilder.build() : Collections.emptyList()));
        return this;
    }

    public FtlBuilder list(String item, String listExpr, Consumer<FtlBuilder> body) {
        FtlBuilder childBuilder = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);

        // 只有在有上下文时才记录变量引用和管理作用域
        if (context != null && validationRecorder != null) {
            // 记录列表表达式变量引用
            boolean isLocal = validationRecorder.isInScope(getRootVariable(listExpr));
            String localVarType = isLocal ? validationRecorder.getScopeType(getRootVariable(listExpr)) : null;
            VariableReference ref = new VariableReference(listExpr, isLocal, localVarType, getCurrentLocation());
            validationRecorder.record(ref);
            // 推入作用域 - item 是列表项变量
            String itemType = getRootVariable(listExpr) + "." + item;
            validationRecorder.pushScope(item, itemType);
            // 使用特殊的标记对象表示这是一个作用域变量
            validationRecorder.assign(item, new ScopeVariableMarker(itemType));
            validationRecorder.recordScopeVariable(item); // 记录这是一个作用域变量
        }
        body.accept(childBuilder);
        nodes.add(new ListNode(item, listExpr, childBuilder.build()));
        return this;
    }

    public FtlBuilder macro(String name, Map<String, String> params, Consumer<FtlBuilder> body) {
        FtlBuilder childBuilder = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);

        // 只有在有上下文时才管理宏作用域
        if (context != null && validationRecorder != null) {
            // 推入宏作用域
            validationRecorder.pushScope(name, "macro");
            // 注册宏参数
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    validationRecorder.assign(entry.getKey(), entry.getValue());
                }
            }
        }
        body.accept(childBuilder);
        nodes.add(new MacroNode(name, params, childBuilder.build()));
        return this;
    }

    // 内部使用 - 创建无上下文的子构建器
    private static FtlBuilder createChildWithoutContext(ValidationRecorder validationRecorder) {
        return new FtlBuilder(null, validationRecorder, false);
    }

    public FtlBuilder callMacro(String name, Map<String, FtlExpr> args) {
        nodes.add(new MacroCallNode(name, args));
        return this;
    }

    public FtlBuilder include(String template, Map<String, FtlExpr> params) {
        nodes.add(new IncludeNode(template, params));
        return this;
    }

    public FtlBuilder comment(String text) {
        nodes.add(new CommentNode(text));
        return this;
    }

    public FtlBuilder compress(Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new CompressNode(b.build()));
        return this;
    }

    public FtlBuilder escape(String expr, String asVar, Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new EscapeNode(expr, asVar, b.build()));
        return this;
    }

    public FtlBuilder noEscape(Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new NoEscapeNode(b.build()));
        return this;
    }

    public FtlBuilder flush() {
        nodes.add(new FlushNode());
        return this;
    }

    public FtlBuilder attempt(Consumer<FtlBuilder> attemptBody, Consumer<FtlBuilder> recoverBody) {
        FtlBuilder attemptBuilder = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        attemptBody.accept(attemptBuilder);

        FtlBuilder recoverBuilder = null;
        if (recoverBody != null) {
            recoverBuilder = context != null ?
                    createChild(context, validationRecorder) :
                    createChildWithoutContext(validationRecorder);
            recoverBody.accept(recoverBuilder);
        }

        nodes.add(new AttemptNode(attemptBuilder.build(),
                recoverBuilder != null ? recoverBuilder.build() : Collections.emptyList()));
        return this;
    }

    public FtlBuilder switchBlock(String expr, Consumer<FtlBuilder> cases, Consumer<FtlBuilder> defaultBody) {
        FtlBuilder caseBuilder = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        cases.accept(caseBuilder);
        FtlBuilder defaultBuilder = null;
        if (defaultBody != null) {
            defaultBuilder = context != null ?
                    createChild(context, validationRecorder) :
                    createChildWithoutContext(validationRecorder);
            defaultBody.accept(defaultBuilder);
        }
        // 构建 CaseNode 列表
        List<CaseNode> caseNodes = new ArrayList<>();
        for (FtlNode node : caseBuilder.build()) {
            if (node instanceof CaseNode) {
                caseNodes.add((CaseNode) node);
            }
        }
        nodes.add(new SwitchNode(new LiteralExpr(expr), caseNodes,
                defaultBuilder != null ? defaultBuilder.build() : Collections.emptyList()));
        return this;
    }

    public FtlBuilder caseBlock(String value, Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new CaseNode(Collections.singletonList(new LiteralExpr(value)), b.build()));
        return this;
    }

    public FtlBuilder breakBlock() {
        nodes.add(new BreakNode());
        return this;
    }

    public FtlBuilder continueBlock() {
        nodes.add(new ContinueNode());
        return this;
    }

    public FtlBuilder returnBlock(FtlExpr expr) {
        nodes.add(new ReturnNode(expr));
        return this;
    }

    public FtlBuilder stopBlock(FtlExpr message) {
        nodes.add(new StopNode(message));
        return this;
    }

    public FtlBuilder items(Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new ItemsNode(b.build()));
        return this;
    }

    public FtlBuilder sep(Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new SepNode(b.build()));
        return this;
    }

    public FtlBuilder importBlock(String template, String namespaceVar) {
        nodes.add(new ImportNode(template, namespaceVar));
        return this;
    }

    public FtlBuilder visit(String nodeExpr, Map<String, FtlExpr> args) {
        nodes.add(new VisitNode(new IdentifierExpr(nodeExpr), args));
        return this;
    }

    public FtlBuilder recurse(FtlExpr expr) {
        nodes.add(new RecurseNode(expr));
        return this;
    }

    public FtlBuilder fallback() {
        nodes.add(new FallbackNode());
        return this;
    }

    public FtlBuilder nested(Consumer<FtlBuilder> body) {
        FtlBuilder b = context != null ?
                createChild(context, validationRecorder) :
                createChildWithoutContext(validationRecorder);
        body.accept(b);
        nodes.add(new NestedNode(b.build()));
        return this;
    }

    public FtlBuilder local(String var, FtlExpr expr) {
        // 只有在有上下文时才记录局部变量赋值
        if (context != null && validationRecorder != null) {
            // 记录局部变量赋值
            validationRecorder.assign(var, expr);
        }
        nodes.add(new LocalNode(var, expr));
        return this;
    }

    public FtlBuilder global(String var, FtlExpr expr) {
        // 只有在有上下文时才记录全局变量赋值
        if (context != null && validationRecorder != null) {
            // 记录全局变量赋值
            validationRecorder.assign(var, expr);
        }
        nodes.add(new GlobalNode(var, expr));
        return this;
    }

    public FtlBuilder setting(String key, FtlExpr value) {
        nodes.add(new SettingNode(key, value));
        return this;
    }

    // 构建方法
    public List<FtlNode> build() {
        // 只有在有上下文且是根构建器时才执行验证
        if (context != null && isRootBuilder) {
            validate();
            cleanupScopes();
        }
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    private void cleanupScopes() {
        // 单独的清理作用域方法
        // 只有在有上下文且是根构建器时才清理
        if (context != null && validationRecorder != null && isRootBuilder) {
            // 清理所有可能残留的作用域
            while (!validationRecorder.getScopeStack().isEmpty()) {
                validationRecorder.popScope();
            }
        }
    }

    // 语义验证方法（只有在有上下文时才执行）
    private void validate() {
        if (context == null || validationRecorder == null) {
            return;
        }
        // 创建验证上下文
        ValidationContext validationContext = new ValidationContext(context, validationRecorder);
        // 从上下文获取验证器链
        VariableValidationChain validationChain = getValidationChain();
        // 收集所有错误
        List<String> allErrors = new ArrayList<>();
        // 验证所有引用的变量
        for (VariableReference ref : validationRecorder.getReferences()) {
            List<String> errors = validationChain.validate(ref, validationContext);
            allErrors.addAll(errors);
        }
        // 如果有错误，抛出异常
        if (!allErrors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Template validation failed:\n");
            for (int i = 0; i < allErrors.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(allErrors.get(i)).append("\n");
            }
            throw new IllegalStateException(sb.toString());
        }

    }
}