package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class NestedNode implements FtlNode {

    private final List<FtlNode> body;


    @JsonCreator
    public NestedNode(@JsonProperty("body") List<FtlNode> body) {
        this.body = body;
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Nested{" + body + "}";
    }
}
