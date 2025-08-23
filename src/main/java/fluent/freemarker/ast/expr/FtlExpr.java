package fluent.freemarker.ast.expr;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = BinaryExpr.class, name = "Binary"),
        @JsonSubTypes.Type(value = IdentifierExpr.class, name = "Identifier"),
        @JsonSubTypes.Type(value = LiteralExpr.class, name = "Literal"),
        @JsonSubTypes.Type(value = RawExpr.class, name = "Raw")})
public interface FtlExpr {
}
