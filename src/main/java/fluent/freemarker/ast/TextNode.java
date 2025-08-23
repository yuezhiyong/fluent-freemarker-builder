package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@Getter
@JsonTypeName("TEXT")
public class TextNode implements FtlNode {

    private final String text;

    @JsonCreator
    public TextNode(@JsonProperty("text") String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "Text{" + text + "}";
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }
}
