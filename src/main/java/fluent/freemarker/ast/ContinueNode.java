package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class ContinueNode implements FtlNode {


    @JsonCreator
    public ContinueNode(){}

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Continue";
    }
}
