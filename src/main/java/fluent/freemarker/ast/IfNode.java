package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class IfNode implements FtlNode {

    public final String condition;
    public final List<FtlNode> thenBlock;
    public final List<FtlNode> elseBlock;

    @JsonCreator
    public IfNode(@JsonProperty("condition") String condition,
                  @JsonProperty("thenBlock") List<FtlNode> thenBlock,
                  @JsonProperty("elseBlock") List<FtlNode> elseBlock) {
        this.condition = condition;
        this.thenBlock = Collections.unmodifiableList(thenBlock);
        this.elseBlock = elseBlock == null
                ? Collections.<FtlNode>emptyList()
                : Collections.unmodifiableList(elseBlock);
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "If(" + condition + ")" + super.toString();
    }
}
