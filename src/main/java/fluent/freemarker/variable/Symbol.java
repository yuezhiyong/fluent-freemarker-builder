package fluent.freemarker.variable;

public class Symbol {
    public final String name;     // 最终变量名
    public final Class<?> type;   // 绑定到的 Java 类型
    public final Scope scope;     // GLOBAL / MACRO / LOCAL / LOOP
    public Symbol(String name, Class<?> type, Scope scope) {
        this.name = name; this.type = type; this.scope = scope;
    }


    public enum Scope { GLOBAL, MACRO, LOCAL, LOOP }
}
