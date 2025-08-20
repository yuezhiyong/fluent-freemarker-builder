package fluent.freemarker.variable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ValidationRecorder {
    private final List<VariableReference> references = new ArrayList<>();
    private final Deque<LocalScope> scopeStack = new ArrayDeque<>();

    public void pushScope(String localVar, String typeName) {
        scopeStack.push(new LocalScope(localVar, typeName));
    }

    public void popScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    public boolean isInScope(String varName) {
        return scopeStack.stream().anyMatch(s -> s.varName.equals(varName));
    }

    public String getScopeType(String varName) {
        for (LocalScope scope : scopeStack) {
            if (scope.varName.equals(varName)) {
                return scope.typeName;
            }
        }
        return null;
    }

    public void record(VariableReference ref) {
        references.add(ref);
    }

    public List<VariableReference> getReferences() {
        return new ArrayList<>(references);
    }

    // 清除（可选）
    public void clear() {
        references.clear();
        scopeStack.clear();
    }

    private static class LocalScope {
        final String varName;
        final String typeName;

        LocalScope(String varName, String typeName) {
            this.varName = varName;
            this.typeName = typeName;
        }
    }

}
