package fluent.freemarker.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fluent.freemarker.ast.FtlNode;
import fluent.freemarker.exception.TemplateSyntaxException;

import java.util.List;

public class AstJson {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static String toJson(List<FtlNode> root) {
        try {
            // Use writeValueAsString with explicit type to trigger polymorphic serialization
            return MAPPER.writerFor(MAPPER.getTypeFactory().constructCollectionType(List.class, FtlNode.class))
                         .writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new TemplateSyntaxException(e);
        }
    }

    public static List<FtlNode> fromJson(String json) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, FtlNode.class));
        } catch (JsonProcessingException e) {
            throw new TemplateSyntaxException(e);
        }
    }


}