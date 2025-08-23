package fluent.freemarker.ast;

public interface FtlVisitor {

    void visit(TextNode textNode);

    void visit(VarNode varNode);

    void visit(IfNode ifNode);

    void visit(MacroNode macroNode);

    void visit(ListNode listNode);

    void visit(AssignNode assignNode);

    void visit(NewlineNode newlineNode);

    void visit(CommentNode commentNode);

    void visit(CompressNode compressNode);

    void visit(EscapeNode escapeNode);

    void visit(NoEscapeNode noEscapeNode);

    void visit(FlushNode flushNode);

    void visit(AttemptNode attemptNode);

    void visit(CaseNode caseNode);

    void visit(SwitchNode switchNode);

    void visit(BreakNode breakNode);

    void visit(ContinueNode continueNode);

    void visit(ReturnNode returnNode);

    void visit(StopNode stopNode);

    void visit(SepNode sepNode);

    void visit(IncludeNode includeNode);

    void visit(ImportNode importNode);

    void visit(VisitNode visitNode);

    void visit(RecurseNode recurseNode);

    void visit(FallbackNode fallbackNode);

    void visit(NestedNode nestedNode);

    void visit(GlobalNode globalNode);

    void visit(LocalNode localNode);

    void visit(SettingNode settingNode);

    void visit(MacroCallNode macroCallNode);

    void visit(ItemsNode itemsNode);

    default void enter(FtlNode node) {
    }


    default void leave(FtlNode node) {
    }
}
