package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextNode.class, name = "Text"),
        @JsonSubTypes.Type(value = VarNode.class, name = "Var"),
        @JsonSubTypes.Type(value = IfNode.class, name = "If"),
        @JsonSubTypes.Type(value = ListNode.class, name = "List"),
        @JsonSubTypes.Type(value = MacroNode.class, name = "Macro"),
        @JsonSubTypes.Type(value = AssignNode.class, name = "Assign"),
        @JsonSubTypes.Type(value = CommentNode.class, name = "Comment"),
        @JsonSubTypes.Type(value = CompressNode.class, name = "Compress"),
        @JsonSubTypes.Type(value = EscapeNode.class, name = "Escape"),
        @JsonSubTypes.Type(value = NoEscapeNode.class, name = "NoEscape"),
        @JsonSubTypes.Type(value = FlushNode.class, name = "Flush"),
        @JsonSubTypes.Type(value = AttemptNode.class, name = "Attempt"),
        @JsonSubTypes.Type(value = SwitchNode.class, name = "Switch"),
        @JsonSubTypes.Type(value = CaseNode.class, name = "Case"),
        @JsonSubTypes.Type(value = BreakNode.class, name = "Break"),
        @JsonSubTypes.Type(value = ContinueNode.class, name = "Continue"),
        @JsonSubTypes.Type(value = ReturnNode.class, name = "Return"),
        @JsonSubTypes.Type(value = StopNode.class, name = "Stop"),
        @JsonSubTypes.Type(value = ItemsNode.class, name = "Items"),
        @JsonSubTypes.Type(value = SepNode.class, name = "Sep"),
        @JsonSubTypes.Type(value = IncludeNode.class, name = "Include"),
        @JsonSubTypes.Type(value = ImportNode.class, name = "Import"),
        @JsonSubTypes.Type(value = VisitNode.class, name = "Visit"),
        @JsonSubTypes.Type(value = RecurseNode.class, name = "Recurse"),
        @JsonSubTypes.Type(value = FallbackNode.class, name = "Fallback"),
        @JsonSubTypes.Type(value = NestedNode.class, name = "Nested"),
        @JsonSubTypes.Type(value = GlobalNode.class, name = "Global"),
        @JsonSubTypes.Type(value = LocalNode.class, name = "Local"),
        @JsonSubTypes.Type(value = SettingNode.class, name = "Setting")
})
public interface FtlNode {

    void accept(FtlVisitor visitor);

    default String nodeInfo() {
        return toString();
    }
}
