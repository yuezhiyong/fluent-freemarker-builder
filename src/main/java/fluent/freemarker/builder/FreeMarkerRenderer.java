package fluent.freemarker.builder;

import fluent.freemarker.ast.*;
import fluent.freemarker.ast.expr.FtlExpr;

import java.util.List;
import java.util.Map;

public class FreeMarkerRenderer extends FtlBaseVisitor {

    private final StringBuilder sb = new StringBuilder();

    private void append(String line) {
        sb.append(line);
    }

    public String render(List<FtlNode> nodes) {
        for (FtlNode n : nodes) {
            n.accept(this);
        }
        return sb.toString();
    }

    @Override
    public void visit(TextNode node) {
        sb.append(node.getText());
    }

    @Override
    public void visit(VarNode node) {
        sb.append("${").append(node.getExpression()).append("}");
    }

    @Override
    public void visit(AssignNode node) {
        append("<#assign " + node.getVarName() + " = " + node.getValueExpr() + ">");
    }

    @Override
    public void visit(IfNode node) {
        append("<#if " + node.condition + ">");
        for (FtlNode n : node.thenBlock) n.accept(this);
        if (!node.elseBlock.isEmpty()) {
            append("<#else>");
            for (FtlNode n : node.elseBlock) n.accept(this);
        }
        append("</#if>");
    }

    @Override
    public void visit(ListNode node) {
        append("<#list " + node.listExpression + " as " + node.item + ">");
        for (FtlNode n : node.body) n.accept(this);
        append("</#list>");
    }

    @Override
    public void visit(MacroNode node) {
        StringBuilder params = new StringBuilder();

        for (Map.Entry<String, String> entry : node.params.entrySet()) {
            params.append(entry.getKey());
            if (entry.getKey() != null) {
                params.append("=").append("\"").append(entry.getValue()).append("\"");
            }
            params.append(" ");
        }
        append("<#macro " + node.name + " " + params.toString().trim() + ">");
        for (FtlNode n : node.body) n.accept(this);
        append("</#macro>");
    }

    @Override
    public void visit(MacroCallNode node) {
        StringBuilder args = new StringBuilder();
        if (node.args != null) {
            for (Map.Entry<String, FtlExpr> entry : node.args.entrySet()) {
                args.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
            }
        }
        append("<@" + node.name + " " + args.toString().trim() + " />");
    }

    @Override
    public void visit(IncludeNode node) {
        append("<#include \"" + node.template + "\">");
    }

    @Override
    public void visit(CommentNode node) {
        append("<#-- " + node.getText() + " -->");
    }

    @Override
    public void visit(CompressNode node) {
        append("<#compress>");
        for (FtlNode n : node.getBody()) n.accept(this);
        append("</#compress>");
    }

    @Override
    public void visit(EscapeNode node) {
        append("<#escape " + node.expr + " as " + node.asVar + ">");
        for (FtlNode n : node.body) n.accept(this);
        append("</#escape>");
    }

    @Override
    public void visit(NoEscapeNode node) {
        append("<#noescape>");
        for (FtlNode n : node.body) n.accept(this);
        append("</#noescape>");
    }

    @Override
    public void visit(FlushNode node) {
        append("<#flush>");
    }

    @Override
    public void visit(AttemptNode node) {
        append("<#attempt>");
        for (FtlNode n : node.attemptBody) n.accept(this);
        if (!node.recoverBody.isEmpty()) {
            append("<#recover>");
            for (FtlNode n : node.recoverBody) n.accept(this);
            append("</#recover>\n");
        }
        append("</#attempt>");
    }

    @Override
    public void visit(SwitchNode node) {
        append("<#switch " + node.expr + ">");
        for (CaseNode c : node.cases) c.accept(this);
        if (!node.defaultBody.isEmpty()) {
            append("<#default>");
            for (FtlNode n : node.defaultBody) n.accept(this);
        }
        append("</#switch>");
    }

    @Override
    public void visit(CaseNode node) {
        StringBuilder values = new StringBuilder();
        for (FtlExpr value : node.values) {
            values.append(value).append(" ");
        }
        append("<#case " + values.toString().trim() + ">");
        for (FtlNode n : node.body) n.accept(this);
    }

    @Override
    public void visit(BreakNode node) {
        append("<#break>");
    }

    @Override
    public void visit(ContinueNode node) {
        append("<#continue>");
    }

    @Override
    public void visit(ReturnNode node) {
        append("<#return " + node.getExpr() + ">");
    }

    @Override
    public void visit(StopNode node) {
        append("<#stop " + node.getExpr() + ">");
    }

    @Override
    public void visit(ItemsNode node) {
        append("<#items>");
        for (FtlNode n : node.body) n.accept(this);
        append("</#items>");
    }

    @Override
    public void visit(SepNode node) {
        append("<#sep>");
        for (FtlNode n : node.body) n.accept(this);
        append("</#sep>");
    }

    @Override
    public void visit(ImportNode node) {
        append("<#import \"" + node.template + "\" as " + node.namespaceVar + ">");
    }

    @Override
    public void visit(VisitNode node) {
        if (node == null || node.args.entrySet() == null) {
            return;
        }
        StringBuilder args = new StringBuilder();
        for (Map.Entry<String, FtlExpr> entry : node.args.entrySet()) {
            args.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }
        append("<#visit " + node.nodeExpr + " " + args.toString().trim() + ">");
    }

    @Override
    public void visit(RecurseNode node) {
        append("<#recurse " + node.expr + ">");
    }

    @Override
    public void visit(FallbackNode node) {
        append("<#fallback>");
    }

    @Override
    public void visit(NestedNode node) {
        append("<#nested>");
        for (FtlNode n : node.getBody()) n.accept(this);
        append("</#nested>");
    }

    @Override
    public void visit(GlobalNode node) {
        append("<#global " + node.var + " = " + node.expr + ">");
    }

    @Override
    public void visit(LocalNode node) {
        append("<#local " + node.var + " = " + node.expr + ">");
    }

    @Override
    public void visit(SettingNode node) {
        append("<#setting " + node.key + " = " + node.value + ">");
    }

    @Override
    public void visit(NewlineNode newlineNode) {
        append("\n");
    }
}
