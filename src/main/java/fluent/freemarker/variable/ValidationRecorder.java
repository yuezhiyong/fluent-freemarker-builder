package fluent.freemarker.variable;

import fluent.freemarker.model.VarType;
import fluent.freemarker.type.VarKeyType;
import lombok.Getter;

import java.util.*;

public class ValidationRecorder {
    private final List<VariableReference> references = new ArrayList<>();
    private final Deque<FreeScope> scopeStack = new ArrayDeque<>(); // 使用 FreeScope
    private final Map<String, Object> globalVariables = new HashMap<>();

    // ====== 变量引用记录 ======
    public void record(VariableReference ref) {
        references.add(ref);
    }

    public List<VariableReference> getReferences() {
        return new ArrayList<>(references);
    }

    // ====== 作用域管理 ======

    /**
     * 推入新作用域
     */
    public void pushScope(String scopeType, String scopeName) {
        scopeStack.push(new FreeScope(scopeType, scopeName));
    }

    /**
     * 推入默认作用域
     */
    public void pushScope() {
        scopeStack.push(new FreeScope("unknown", "unnamed"));
    }

    /**
     * 弹出作用域
     */
    public void popScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    /**
     * 在当前作用域中定义变量
     */
    public void defineVariable(String name, Object value) {
        if (scopeStack.isEmpty()) {
            // 如果没有作用域，定义为全局变量
            globalVariables.put(name, value);
        } else {
            // 在当前作用域中定义
            scopeStack.peek().defineVariable(name, value);
        }
    }

    /**
     * 检查变量是否已定义（从内到外查找）
     */
    public boolean isDefined(String name) {
        // 检查作用域（从内到外）
        for (FreeScope scope : scopeStack) {
            if (scope.containsVariable(name)) {
                return true;
            }
        }
        // 检查全局变量
        return globalVariables.containsKey(name);
    }

    /**
     * 获取变量值（从内到外查找）
     */
    public Object getValue(String name) {
        // 检查作用域（从内到外）
        for (FreeScope scope : scopeStack) {
            if (scope.containsVariable(name)) {
                return scope.getVariable(name);
            }
        }
        // 检查全局变量
        return globalVariables.get(name);
    }

    // ====== 特定作用域检查 ======

    /**
     * 检查变量是否在全局作用域中定义
     */
    public boolean isDefinedGlobally(String name) {
        return globalVariables.containsKey(name);
    }

    /**
     * 检查变量是否在局部作用域中定义
     */
    public boolean isDefinedInScope(String name) {
        // 检查所有局部作用域
        for (FreeScope scope : scopeStack) {
            if (scope.containsVariable(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否是特殊标记的变量（如作用域变量）
     */
    public boolean isMarkedVariable(String name) {
        Object value = getValue(name);
        return value instanceof ScopeVariableMarker;
    }

    /**
     * 检查是否是作用域变量
     */
    public boolean isScopeVariable(String name) {
        // 检查所有作用域中的变量是否是 ScopeMarker
        for (FreeScope scope : scopeStack) {
            if (scope.containsVariable(name)) {
                Object value = scope.getVariable(name);
                return value instanceof ScopeVariableMarker;
            }
        }
        return false;
    }

    /**
     * 获取变量的作用域类型
     */
    public VarKeyType getVariableScopeType(String name) {
        for (FreeScope scope : scopeStack) {
            if (scope.containsVariable(name)) {
                Object value = scope.getVariable(name);
                if (value instanceof ScopeVariableMarker) {
                    return VarKeyType.ofKeyType(name, ((ScopeVariableMarker) value).getType());
                } else if (value != null) {
                    return VarKeyType.ofKeyType(name, value.getClass().getSimpleName());
                }
                return VarKeyType.ofKeyType(name, "object");
            }
        }
        return VarKeyType.ofKeyType(name, VarType.UNDEFINED.name());
    }

    // ====== 上下文集成 ======
    public void wrapContext(FluentFreemarkerContext context) {
        if (context != null) {
            Map<String, Object> ctxMap = context.getContext();
            if (ctxMap != null) {
                for (Map.Entry<String, Object> entry : ctxMap.entrySet()) {
                    globalVariables.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
        }
    }


    public Set<String> getAllDefinedVariables() {
        Set<String> all = new HashSet<>(globalVariables.keySet());
        for (FreeScope scope : scopeStack) {
            all.addAll(scope.getVariableNames());
        }
        return all;
    }

    public int getScopeDepth() {
        return scopeStack.size();
    }


    // ====== 内部类：局部作用域 ======
    @Getter
    public static class FreeScope {
        private final Map<String, Object> variables = new HashMap<>();
        private final String scopeType; // 作用域类型：list, macro, function, etc.
        private final String scopeName; // 作用域名称（如列表变量名、宏名等）

        public FreeScope(String scopeType, String scopeName) {
            this.scopeType = scopeType != null ? scopeType : "unknown";
            this.scopeName = scopeName != null ? scopeName : "unnamed";
        }

        /**
         * 在作用域中定义变量
         */
        public void defineVariable(String name, Object value) {
            variables.put(name, value);
        }

        /**
         * 检查变量是否在当前作用域中定义
         */
        public boolean containsVariable(String name) {
            return variables.containsKey(name);
        }

        /**
         * 获取作用域中的变量值
         */
        public Object getVariable(String name) {
            return variables.get(name);
        }

        /**
         * 获取作用域中所有变量名
         */
        public Set<String> getVariableNames() {
            return new HashSet<>(variables.keySet());
        }

        /**
         * 获取作用域中所有变量
         */
        public Map<String, Object> getVariables() {
            return new HashMap<>(variables);
        }


        @Override
        public String toString() {
            return "FreeScope{" +
                    "type='" + scopeType + '\'' +
                    ", name='" + scopeName + '\'' +
                    ", variables=" + variables.size() +
                    '}';
        }
    }

    // ====== 工具方法 ======
    public void clear() {
        references.clear();
        scopeStack.clear();
        globalVariables.clear();
    }

    public Set<String> getGlobalAssignedVars() {
        return Collections.unmodifiableSet(globalVariables.keySet());
    }

    public Deque<FreeScope> getScopeStack() {
        return new ArrayDeque<>(scopeStack);
    }


}
