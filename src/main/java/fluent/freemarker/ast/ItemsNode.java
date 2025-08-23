package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ItemsNode implements FtlNode {
    public final List<FtlNode> body;

    @JsonCreator
    public ItemsNode(@JsonProperty("body") List<FtlNode> body) {
        this.body = body == null
                ? Collections.<FtlNode>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(body));
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Items{" + body + "}";
    }
}
