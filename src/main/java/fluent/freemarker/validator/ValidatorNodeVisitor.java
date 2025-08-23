package fluent.freemarker.validator;

public interface ValidatorNodeVisitor {
    void visit(ValidatorNode node, int index);
}
