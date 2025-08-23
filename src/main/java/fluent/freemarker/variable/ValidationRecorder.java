package fluent.freemarker.variable;

import java.util.*;

public class ValidationRecorder {
    private final List<VariableReference> references = new ArrayList<>();
    private final Deque<LocalScope> scopeStack = new ArrayDeque<>();
    private final Set<String> globalAssignedVars = new HashSet<>();
    private final Map<String, Object> assignedValues = new HashMap<>();
    private final Set<String> scopeVariables = new HashSet<>(); // 记录所有作用域变量

    // ====== 变量引用 ======
    public void record(VariableReference ref) {
        references.add(ref);
    }


    public void wrapCtx(FluentFreemarkerContext context) {
        Map<String, Object> ctxMap = context.getContext();
        if (ctxMap != null) {
            for (Map.Entry<String, Object> entry : ctxMap.entrySet()) {
                assign(entry.getKey(), entry.getValue());
            }
        }
    }

    public List<VariableReference> getReferences() {
        return new ArrayList<>(references);
    }

    // ====== 作用域管理 ======
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

    // 记录作用域变量
    public void recordScopeVariable(String varName) {
        scopeVariables.add(varName);
    }

    // 检查是否是作用域变量
    public boolean isScopeVariable(String varName) {
        return scopeVariables.contains(varName);
    }


    public boolean isDefinedGlobally(String varName) {
        return globalAssignedVars.contains(varName);
    }

    /**
     * 检查变量是否在当前作用域中定义（非全局）
     */
    public boolean isDefinedInScope(String name) {
        // 检查所有作用域（除了全局作用域）
        for (LocalScope scope : scopeStack) {
            if (scope.assignedVars.contains(name)) {
                return true;
            }
        }
        return false;
    }

    // 获取所有作用域变量（用于调试）
    public Set<String> getScopeVariables() {
        return new HashSet<>(scopeVariables);
    }

    // ====== assign 变量管理 ======
    public void assign(String varName, Object value) {
        if (scopeStack.isEmpty()) {
            globalAssignedVars.add(varName);
            assignedValues.put(varName, value);  // 存储值
        } else {
            LocalScope scope = scopeStack.peek();
            scope.assignedVars.add(varName);
            scope.assignedValues.put(varName, value);
        }
    }

    public Object getAssignedValue(String varName) {
        // 从内层作用域向外查找
        for (LocalScope scope : scopeStack) {
            if (scope.assignedValues.containsKey(varName)) {
                return scope.assignedValues.get(varName);
            }
        }
        return assignedValues.get(varName);
    }

    public boolean isAssigned(String varName) {
        // 优先检查作用域内 assign
        for (LocalScope scope : scopeStack) {
            if (scope.assignedVars.contains(varName)) {
                return true;
            }
        }
        return globalAssignedVars.contains(varName);
    }


    // ====== 内部类：局部作用域 ======
    public static class LocalScope {
        final String varName;
        final String typeName;
        final Set<String> assignedVars = new HashSet<>();
        final Map<String, Object> assignedValues = new HashMap<>();  // 新增

        public LocalScope(String varName, String typeName) {
            this.varName = varName;
            this.typeName = typeName;
        }

        @Override
        public String toString() {
            return "LocalScope{var='" + varName + "', type='" + typeName + "', assigned=" + assignedVars + "}";
        }
    }

    // ====== 工具方法 ======
    public void clear() {
        references.clear();
        scopeStack.clear();
        globalAssignedVars.clear();
    }

    public Set<String> getGlobalAssignedVars() {
        return Collections.unmodifiableSet(globalAssignedVars);
    }

    public Deque<LocalScope> getScopeStack() {
        return new ArrayDeque<>(scopeStack);
    }


}
