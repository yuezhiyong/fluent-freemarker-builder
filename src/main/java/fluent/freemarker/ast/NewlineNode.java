package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class NewlineNode implements FtlNode {

    public static final NewlineNode INSTANCE = new NewlineNode();


    @JsonCreator
    private NewlineNode() {
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Newline";
    }
}
