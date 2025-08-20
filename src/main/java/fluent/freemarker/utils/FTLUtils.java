package fluent.freemarker.utils;

import fluent.freemarker.variable.VariablePath;

public class FTLUtils {

    /**
     * Repeats the given string n times.
     * Compatible with Java 8.
     */
    public static String repeat(String str, int times) {
        if (times <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }


    public static String spaces(int times) {
        return repeat(" ", times);
    }


    /**
     * 计算当前路径与另一个路径的 Levenshtein 编辑距离
     */
    public static int editDistanceTo(VariablePath me, VariablePath other) {
        return editDistance(me.toString(), other.toString());
    }

    /**
     * 计算两个字符串的 Levenshtein Distance
     */
    private static int editDistance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]),     // 删除 / 插入
                            dp[i - 1][j - 1]                          // 替换
                    );
                }
            }
        }
        return dp[m][n];
    }

}
