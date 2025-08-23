package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class ListNode implements FtlNode {

    public final String item;
    public final String listExpression;
    public final List<FtlNode> body;

    @JsonCreator
    public ListNode(@JsonProperty("item") String item, @JsonProperty("listExpression") String listExpression, @JsonProperty("body") List<FtlNode> body) {
        this.item = item;
        this.listExpression = listExpression;
        this.body = Collections.unmodifiableList(body);
    }

    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "List(" + listExpression + " as " + item + ")" + super.toString();
    }
}
