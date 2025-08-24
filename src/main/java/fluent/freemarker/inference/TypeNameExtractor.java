package fluent.freemarker.inference;

public interface TypeNameExtractor {

    /**
     * 从复合类型名称中提取元素类型名称
     */
    String extractElementTypeName(String collectionTypeName);

    /**
     * 从命名规则推断单数形式
     */
    String inferSingularForm(String pluralName);

    /**
     * 添加自定义的单复数映射规则
     */
    void addPluralRule(String plural, String singular);
}
