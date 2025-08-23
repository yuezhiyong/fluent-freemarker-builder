package fluent.freemarker.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ValidatorNode {

    private final VariableValidator validator;
    @Setter
    private ValidatorNode next;
    @Setter
    private ValidatorNode prev;

    public ValidatorNode(VariableValidator validator) {
        this.validator = validator;
    }


    // 链表操作方法
    public void insertAfter(ValidatorNode newNode) {
        newNode.next = this.next;
        newNode.prev = this;
        if (this.next != null) {
            this.next.prev = newNode;
        }
        this.next = newNode;
    }

    public void insertBefore(ValidatorNode newNode) {
        newNode.next = this;
        newNode.prev = this.prev;
        if (this.prev != null) {
            this.prev.next = newNode;
        }
        this.prev = newNode;
    }

    public void remove() {
        if (this.prev != null) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
        this.prev = null;
        this.next = null;
    }

}
