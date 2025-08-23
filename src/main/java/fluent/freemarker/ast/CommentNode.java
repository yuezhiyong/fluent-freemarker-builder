package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommentNode implements FtlNode {

    private final String text;

    @JsonCreator
    public CommentNode(@JsonProperty("text") String text) {
        this.text = text;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Comment{" + text + "}";
    }
}
