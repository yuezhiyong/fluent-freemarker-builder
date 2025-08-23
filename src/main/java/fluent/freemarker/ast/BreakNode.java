package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class BreakNode implements FtlNode {

    @JsonCreator
    public BreakNode() {

    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Break";
    }
}
