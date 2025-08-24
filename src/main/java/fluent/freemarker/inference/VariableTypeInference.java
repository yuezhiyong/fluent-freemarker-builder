package fluent.freemarker.inference;

import fluent.freemarker.model.TypeInfo;
import fluent.freemarker.validator.ValidationContext;

public interface VariableTypeInference {
    /**
     * 推断变量的类型信息
     */
    TypeInfo inferType(String varName, Object value, ValidationContext context);

    /**
     * 从集合类型推断元素类型
     */
    TypeInfo inferElementType(TypeInfo collectionTypeInfo, ValidationContext context);

    /**
     * 从变量名称推断类型名称
     */
    String inferTypeNameFromName(String varName);
}
