package fluent.freemarker.utils;

public class PathUtils {

    private PathUtils() {
    }

    public static String getRootVariable(String expression) {
        if (expression == null || expression.isEmpty()) return "";
        int dotIndex = expression.indexOf('.');
        return dotIndex > 0 ? expression.substring(0, dotIndex) : expression;
    }
}
