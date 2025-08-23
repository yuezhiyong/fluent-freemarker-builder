package fluent.freemarker.variable;

import lombok.Getter;

@Getter
public class ScopeVariableMarker {

    private final String type;

    public ScopeVariableMarker(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "ScopeVariable[" + type + "]";
    }
}
