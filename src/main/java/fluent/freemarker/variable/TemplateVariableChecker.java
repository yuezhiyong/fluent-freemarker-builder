package fluent.freemarker.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateVariableChecker {
    // 匹配 ${...}，支持嵌套点号：${user.name}, ${order.items[0].id}
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private final VariableRegistry registry;

    public TemplateVariableChecker(VariableRegistry registry) {
        this.registry = registry;
    }

    public List<String> findUnknownVariables(String template) {
        List<String> unknown = new ArrayList<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String expr = matcher.group(1).trim();
            if (expr.isEmpty()) continue;
            if (!registry.knows(expr)) {
                unknown.add(expr);
            }
        }
        return unknown;
    }

    public boolean isValid(String template) {
        return findUnknownVariables(template).isEmpty();
    }

    public List<String> getSuggestions(String unknownPath) {
        return registry.findSuggestions(unknownPath).stream()
                .map(VariablePath::toString)
                .limit(5)
                .collect(Collectors.toList());
    }
}
