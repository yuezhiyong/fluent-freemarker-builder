package fluent.freemarker.variable;

import lombok.Getter;

@Getter
public final class ScopeVariable {
    private final String type;
    private final String definedAt;

    public ScopeVariable(String type, String definedAt) {
        this.type = type;
        this.definedAt = definedAt;
    }

    public String getType() {
        return type;
    }

    public String getDefinedAt() {
        return definedAt;
    }

    @Override
    public String toString() {
        return "SCOPE_VARIABLE:" + type;
    }
}
