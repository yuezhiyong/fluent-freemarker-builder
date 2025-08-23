package fluent.freemarker.builder;

import fluent.freemarker.ast.*;
import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.IdentifierExpr;
import fluent.freemarker.ast.expr.LiteralExpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FtlBuilder {

    private final List<FtlNode> nodes = new ArrayList<>();

    public static FtlBuilder create() {
        return new FtlBuilder();
    }

    public FtlBuilder text(String text) {
        nodes.add(new TextNode(text));
        return this;
    }

    public FtlBuilder var(String name) {
        nodes.add(new VarNode(name));
        return this;
    }

    public FtlBuilder ifBlock(String condition, Consumer<FtlBuilder> thenBody) {
        return ifElseBlock(condition, thenBody, null);
    }

    public FtlBuilder ifElseBlock(String condition, Consumer<FtlBuilder> thenBody, Consumer<FtlBuilder> elseBody) {
        FtlBuilder then = new FtlBuilder();
        thenBody.accept(then);
        FtlBuilder el = new FtlBuilder();
        if (elseBody != null) elseBody.accept(el);
        nodes.add(new IfNode(condition, then.build(), el.build()));
        return this;
    }

    public FtlBuilder list(String item, String listExpr, Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new ListNode(item, listExpr, b.build()));
        return this;
    }

    public FtlBuilder macro(String name, Map<String, String> params, Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new MacroNode(name, params, b.build()));
        return this;
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
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new CompressNode(b.build()));
        return this;
    }

    public FtlBuilder escape(String expr, String asVar, Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new EscapeNode(expr, asVar, b.build()));
        return this;
    }

    public FtlBuilder noEscape(Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new NoEscapeNode(b.build()));
        return this;
    }

    public FtlBuilder flush() {
        nodes.add(new FlushNode());
        return this;
    }

    public FtlBuilder attempt(Consumer<FtlBuilder> attemptBody, Consumer<FtlBuilder> recoverBody) {
        FtlBuilder attempt = new FtlBuilder();
        attemptBody.accept(attempt);
        FtlBuilder recover = new FtlBuilder();
        if (recoverBody != null) recoverBody.accept(recover);
        nodes.add(new AttemptNode(attempt.build(), recover.build()));
        return this;
    }

    public FtlBuilder switchBlock(String expr, Consumer<FtlBuilder> cases, Consumer<FtlBuilder> defaultBody) {
        FtlBuilder caseBuilder = new FtlBuilder();
        cases.accept(caseBuilder);
        FtlBuilder defaultBuilder = new FtlBuilder();
        if (defaultBody != null) defaultBody.accept(defaultBuilder);
        // 构建 CaseNode 列表
        List<CaseNode> caseNodes = new ArrayList<>();
        for (FtlNode node : caseBuilder.build()) {
            if (node instanceof CaseNode) {
                caseNodes.add((CaseNode) node);
            }
        }
        nodes.add(new SwitchNode(new LiteralExpr(expr), caseNodes, defaultBuilder.build()));
        return this;
    }

    public FtlBuilder caseBlock(String value, Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
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
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new ItemsNode(b.build()));
        return this;
    }

    public FtlBuilder sep(Consumer<FtlBuilder> body) {
        FtlBuilder b = new FtlBuilder();
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
        FtlBuilder b = new FtlBuilder();
        body.accept(b);
        nodes.add(new NestedNode(b.build()));
        return this;
    }

    public FtlBuilder global(String var, FtlExpr expr) {
        nodes.add(new GlobalNode(var, expr));
        return this;
    }

    public FtlBuilder local(String var, FtlExpr expr) {
        nodes.add(new LocalNode(var, expr));
        return this;
    }

    public FtlBuilder setting(String key, FtlExpr value) {
        nodes.add(new SettingNode(key, value));
        return this;
    }

    public List<FtlNode> build() {
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }
}
