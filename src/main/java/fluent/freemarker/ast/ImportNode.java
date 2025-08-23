package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ImportNode implements FtlNode {
    public final String template;
    public final String namespaceVar;

    @JsonCreator
    public ImportNode(@JsonProperty("template") String template, @JsonProperty("namespaceVar") String namespaceVar) {
        this.template = template;
        this.namespaceVar = namespaceVar;
    }


    @Override
    public void accept(FtlVisitor visitor) {
        visitor.enter(this);
        visitor.visit(this);
        visitor.leave(this);
    }

    @Override
    public String toString() {
        return "Import{" + template + "," + namespaceVar + "}";
    }
}
