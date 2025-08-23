package fluent.freemarker.ast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MacroNode implements FtlNode {
    public final String name;
    public final Map<String, String> params;
    public final List<FtlNode> body;

    @JsonCreator
    public MacroNode(@JsonProperty("name") String name, @JsonProperty("params") Map<String, String> params, @JsonProperty("body") List<FtlNode> body) {
        this.name = name;
        this.params = params == null ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<String, String>(params));
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
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("key:").append(entry.getKey()).append(",").append("value:").append(entry.getValue());
        }
        return "Macro{" + name + ", " + sb+ body + "}";
    }
}
