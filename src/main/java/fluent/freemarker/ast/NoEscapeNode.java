package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class NoEscapeNode implements FtlNode {

    public final List<FtlNode> body;

    @JsonCreator
    public NoEscapeNode(@JsonProperty("body") List<FtlNode> body) {
        this.body = Collections.unmodifiableList(new ArrayList<FtlNode>(body));
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "NoEscape{" + body + "}";
    }
}
