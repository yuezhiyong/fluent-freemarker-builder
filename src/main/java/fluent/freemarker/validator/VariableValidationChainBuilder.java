package fluent.freemarker.validator;

public class VariableValidationChainBuilder {
    private final VariableValidationChain chain;

    private VariableValidationChainBuilder() {
        this.chain = new VariableValidationChain();
    }

    public static VariableValidationChainBuilder create() {
        return new VariableValidationChainBuilder();
    }

    public VariableValidationChainBuilder add(VariableValidator validator) {
        chain.addValidator(validator);
        return this;
    }

    public VariableValidationChainBuilder insert(int index, VariableValidator validator) {
        chain.insertValidator(index, validator);
        return this;
    }

    public VariableValidationChain build() {
        return chain;
    }
}
