package fluent.freemarker.validator;

import fluent.freemarker.variable.VariableReference;

import java.util.ArrayList;
import java.util.List;

public class VariableValidationChain {

    private ValidatorNode head;
    private ValidatorNode tail;
    private int size;

    public VariableValidationChain() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // 在链表末尾添加验证器
    public VariableValidationChain addValidator(VariableValidator validator) {
        ValidatorNode newNode = new ValidatorNode(validator);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
        return this;
    }

    // 在指定位置插入验证器
    public VariableValidationChain insertValidator(int index, VariableValidator validator) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == size) {
            return addValidator(validator);
        }

        ValidatorNode newNode = new ValidatorNode(validator);
        if (index == 0) {
            newNode.setNext(head);
            if (head != null) {
                head.setPrev(newNode);
            }
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            ValidatorNode current = getNodeAt(index);
            current.insertBefore(newNode);
        }
        size++;
        return this;
    }

    // 移除指定位置的验证器
    public VariableValidationChain removeValidator(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        ValidatorNode nodeToRemove = getNodeAt(index);
        if (nodeToRemove == head) {
            head = nodeToRemove.getNext();
        }
        if (nodeToRemove == tail) {
            tail = nodeToRemove.getPrev();
        }
        nodeToRemove.remove();
        size--;
        return this;
    }

    // 获取指定位置的节点
    private ValidatorNode getNodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        ValidatorNode current;
        // 优化：从较近的一端开始遍历
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.getPrev();
            }
        }
        return current;
    }

    // 获取验证器数量
    public int size() {
        return size;
    }

    // 验证单个变量引用
    public List<String> validate(VariableReference reference, ValidationContext context) {
        List<String> errors = new ArrayList<>();
        ValidatorNode current = head;
        while (current != null) {
            VariableValidator validator = current.getValidator();
            // 检查是否应该跳过此验证器
            if (validator.skipValidate(reference, context)) {
                current = current.getNext();
                continue;
            }
            ValidationResult result = validator.validate(reference, context);
            if (!result.isValid()) {
                if (result.getErrorMessage() != null) {
                    errors.add(result.getErrorMessage());
                }
                if (!result.isShouldContinue()) {
                    break; // 停止后续验证
                }
            } else {
                // 验证通过
                if (!result.isShouldContinue()) {
                    break;
                }
            }
            current = current.getNext();
        }
        return errors;
    }

    // 获取所有验证器（用于调试或检查）
    public List<VariableValidator> getValidators() {
        List<VariableValidator> validators = new ArrayList<>();
        ValidatorNode current = head;
        while (current != null) {
            validators.add(current.getValidator());
            current = current.getNext();
        }
        return validators;
    }

    // 清空所有验证器
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // 创建默认验证器链
    public static VariableValidationChain createDefaultChain() {
        return new VariableValidationChain()
                .addValidator(new GlobalVariableValidator())
                .addValidator(new ScopeVariableValidator())
                .addValidator(new TypeValidator());
    }

    // 正向遍历验证器
    public void traverseForward(ValidatorNodeVisitor visitor) {
        ValidatorNode current = head;
        int index = 0;
        while (current != null) {
            visitor.visit(current, index);
            current = current.getNext();
            index++;
        }
    }

    // 反向遍历验证器
    public void traverseBackward(ValidatorNodeVisitor visitor) {
        ValidatorNode current = tail;
        int index = size - 1;
        while (current != null) {
            visitor.visit(current, index);
            current = current.getPrev();
            index--;
        }
    }







}
