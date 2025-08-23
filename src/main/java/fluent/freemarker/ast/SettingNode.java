package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fluent.freemarker.ast.expr.FtlExpr;
import lombok.Getter;

@Getter
public class SettingNode implements FtlNode {
    public final String key;
    public final FtlExpr value;

    @JsonCreator
    public SettingNode(@JsonProperty("key") String key, @JsonProperty("value") FtlExpr value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Setting{" + key + "," + value + "}";
    }
}
