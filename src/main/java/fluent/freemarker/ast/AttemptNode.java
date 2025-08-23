package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class AttemptNode implements FtlNode {
    public final List<FtlNode> attemptBody;
    public final List<FtlNode> recoverBody;


    @JsonCreator
    public AttemptNode(@JsonProperty("attemptBody") final List<FtlNode> attemptBody, @JsonProperty("recoverBody") final List<FtlNode> recoverBody) {
        this.attemptBody = attemptBody;
        this.recoverBody = recoverBody;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }


    @Override
    public String toString() {
        return "Attempt{" + attemptBody + "," + recoverBody + "}";
    }
}
