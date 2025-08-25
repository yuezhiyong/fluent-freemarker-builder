package fluent.freemarker.builder;

import fluent.freemarker.ast.*;
import fluent.freemarker.ast.expr.BinaryExpr;
import fluent.freemarker.ast.expr.FtlExpr;
import fluent.freemarker.ast.expr.IdentifierExpr;
import fluent.freemarker.ast.expr.LiteralExpr;

import java.util.List;
import java.util.Map;

public class FreeMarkerRenderer extends FtlBaseVisitor {
    private final StringBuilder sb = new StringBuilder();

    private void append(String line) {
        sb.append(line);
    }

    public String render(List<FtlNode> nodes) {
        sb.setLength(0); // 清空之前的渲染结果
        for (FtlNode n : nodes) {
            if (n != null) {
                n.accept(this);
            }
        }
        return sb.toString();
    }

    @Override
    public void visit(TextNode node) {
        if (node != null && node.getText() != null) {
            sb.append(node.getText());
        }
    }

    @Override
    public void visit(VarNode node) {
        if (node != null && node.getExpression() != null) {
            sb.append("${").append(node.getExpression()).append("}");
        }
    }

    @Override
    public void visit(AssignNode node) {
        if (node != null) {
            append("<#assign " + node.getVarName() + " = " + node.getValueExpr() + ">");
        }
    }

    @Override
    public void visit(IfNode node) {
        if (node != null) {
            append("<#if " + node.getCondition() + ">");
            if (node.getThenBlock() != null) {
                for (FtlNode n : node.getThenBlock()) {
                    if (n != null) n.accept(this);
                }
            }
            if (node.getElseBlock() != null && !node.getElseBlock().isEmpty()) {
                append("<#else>");
                for (FtlNode n : node.getElseBlock()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#if>");
        }
    }

    @Override
    public void visit(ListNode node) {
        if (node != null) {
            append("<#list " + node.getListExpression() + " as " + node.getItem() + ">");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#list>");
        }
    }

    @Override
    public void visit(MacroNode node) {
        if (node != null) {
            StringBuilder params = new StringBuilder();
            if (node.getParams() != null) {
                boolean first = true;
                for (Map.Entry<String, String> entry : node.getParams().entrySet()) {
                    if (!first) params.append(", ");
                    params.append(entry.getKey());
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        params.append("=").append("\"").append(entry.getValue()).append("\"");
                    }
                    first = false;
                }
            }
            append("<#macro " + node.getName());
            if (params.length() > 0) {
                append(" " + params.toString());
            }
            append(">");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#macro>");
        }
    }

    @Override
    public void visit(MacroCallNode node) {
        if (node != null) {
            StringBuilder args = new StringBuilder();
            if (node.getArgs() != null && !node.getArgs().isEmpty()) {
                for (Map.Entry<String, FtlExpr> entry : node.getArgs().entrySet()) {
                    if (args.length() > 0) args.append(" ");
                    args.append(entry.getKey()).append("=").append(renderExpr(entry.getValue()));
                }
            }
            if (args.length() > 0) {
                append("<@" + node.getName() + " " + args.toString() + " />");
            } else {
                append("<@" + node.getName() + " />");
            }
        }
    }

    @Override
    public void visit(IncludeNode node) {
        if (node != null) {
            StringBuilder params = new StringBuilder();
            if (node.getParams() != null && !node.getParams().isEmpty()) {
                for (Map.Entry<String, FtlExpr> entry : node.getParams().entrySet()) {
                    if (params.length() > 0) params.append(" ");
                    params.append(entry.getKey()).append("=").append(renderExpr(entry.getValue()));
                }
            }
            if (params.length() > 0) {
                append("<#include \"" + node.getTemplate() + "\" " + params.toString() + ">");
            } else {
                append("<#include \"" + node.getTemplate() + "\">");
            }
        }
    }

    @Override
    public void visit(CommentNode node) {
        if (node != null && node.getText() != null) {
            append("<#-- " + node.getText() + " -->");
        }
    }

    @Override
    public void visit(CompressNode node) {
        if (node != null) {
            append("<#compress>");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#compress>");
        }
    }

    @Override
    public void visit(EscapeNode node) {
        if (node != null) {
            append("<#escape " + node.getAsVar() + " in " + node.getExpr() + ">");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#escape>");
        }
    }

    @Override
    public void visit(NoEscapeNode node) {
        if (node != null) {
            append("<#noescape>");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#noescape>");
        }
    }

    @Override
    public void visit(FlushNode node) {
        append("<#flush>");
    }

    @Override
    public void visit(AttemptNode node) {
        if (node != null) {
            append("<#attempt>");
            if (node.getAttemptBody() != null) {
                for (FtlNode n : node.getAttemptBody()) {
                    if (n != null) n.accept(this);
                }
            }
            if (node.getRecoverBody() != null && !node.getRecoverBody().isEmpty()) {
                append("<#recover>");
                for (FtlNode n : node.getRecoverBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#attempt>");
        }
    }

    @Override
    public void visit(SwitchNode node) {
        if (node != null) {
            append("<#switch " + renderExpr(node.getExpr()) + ">");
            if (node.getCases() != null) {
                for (CaseNode c : node.getCases()) {
                    if (c != null) c.accept(this);
                }
            }
            if (node.getDefaultBody() != null && !node.getDefaultBody().isEmpty()) {
                append("<#default>");
                for (FtlNode n : node.getDefaultBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#switch>");
        }
    }

    @Override
    public void visit(CaseNode node) {
        if (node != null) {
            StringBuilder values = new StringBuilder();
            if (node.getValues() != null) {
                for (FtlExpr value : node.getValues()) {
                    if (values.length() > 0) values.append(" ");
                    values.append(renderExpr(value));
                }
            }
            if (values.length() > 0) {
                append("<#case " + values.toString() + ">");
            } else {
                append("<#case>");
            }
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
        }
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
        if (node != null) {
            append("<#return " + renderExpr(node.getExpr()) + ">");
        }
    }

    @Override
    public void visit(StopNode node) {
        if (node != null) {
            append("<#stop " + renderExpr(node.getExpr()) + ">");
        }
    }

    @Override
    public void visit(ItemsNode node) {
        if (node != null) {
            append("<#items>");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#items>");
        }
    }

    @Override
    public void visit(SepNode node) {
        if (node != null) {
            append("<#sep>");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#sep>");
        }
    }

    @Override
    public void visit(ImportNode node) {
        if (node != null) {
            append("<#import \"" + node.getTemplate() + "\" as " + node.getNamespaceVar() + ">");
        }
    }

    @Override
    public void visit(VisitNode node) {
        if (node != null) {
            StringBuilder args = new StringBuilder();
            if (node.getArgs() != null) {
                for (Map.Entry<String, FtlExpr> entry : node.getArgs().entrySet()) {
                    if (args.length() > 0) args.append(" ");
                    args.append(entry.getKey()).append("=").append(renderExpr(entry.getValue()));
                }
            }
            String argsStr = args.length() > 0 ? " " + args.toString() : "";
            append("<#visit " + renderExpr(node.getNodeExpr()) + argsStr + ">");
        }
    }

    @Override
    public void visit(RecurseNode node) {
        if (node != null) {
            append("<#recurse " + renderExpr(node.getExpr()) + ">");
        }
    }

    @Override
    public void visit(FallbackNode node) {
        append("<#fallback>");
    }

    @Override
    public void visit(NestedNode node) {
        if (node != null) {
            append("<#nested>");
            if (node.getBody() != null) {
                for (FtlNode n : node.getBody()) {
                    if (n != null) n.accept(this);
                }
            }
            append("</#nested>");
        }
    }

    @Override
    public void visit(GlobalNode node) {
        if (node != null) {
            append("<#global " + node.getVar() + " = " + renderExpr(node.getExpr()) + ">");
        }
    }

    @Override
    public void visit(LocalNode node) {
        if (node != null) {
            append("<#local " + node.getVar() + " = " + renderExpr(node.getExpr()) + ">");
        }
    }

    @Override
    public void visit(SettingNode node) {
        if (node != null) {
            append("<#setting " + node.getKey() + " = " + renderExpr(node.getValue()) + ">");
        }
    }

    @Override
    public void visit(NewlineNode node) {
        append("\n");
    }

    /**
     * 渲染表达式为字符串
     */
    private String renderExpr(FtlExpr expr) {
        if (expr == null) return "";

        if (expr instanceof IdentifierExpr) {
            return ((IdentifierExpr) expr).getName();
        } else if (expr instanceof LiteralExpr) {
            Object value = ((LiteralExpr) expr).getValue();
            if (value instanceof String) {
                return "\"" + value.toString().replace("\"", "\\\"") + "\"";
            } else {
                return value != null ? value.toString() : "null";
            }
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binary = (BinaryExpr) expr;
            return renderExpr(binary.getLeft()) + " " + binary.getOp() + " " + renderExpr(binary.getRight());
        } else {
            return expr.toString();
        }
    }
}
