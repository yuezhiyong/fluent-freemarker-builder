package fluent.freemarker.variable;

import fluent.freemarker.utils.FTLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VariablePath implements Comparable<VariablePath>{
    private final List<String> segments; // 如 ["user", "profile", "name"]

    public VariablePath(String path) {
        this.segments = parse(path);
    }

    public VariablePath(List<String> segments) {
        this.segments = new ArrayList<>(segments);
    }

    public List<String> getSegments() {
        return new ArrayList<>(segments);
    }

    public String toString() {
        return String.join(".", segments);
    }

    public boolean startsWith(VariablePath prefix) {
        if (prefix.segments.size() > this.segments.size()) return false;
        for (int i = 0; i < prefix.segments.size(); i++) {
            if (!this.segments.get(i).equals(prefix.segments.get(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断是否与另一个路径“相似”（编辑距离 <= maxDistance）
     */
    public boolean isSimilarTo(VariablePath other, int maxDistance) {
        return FTLUtils.editDistanceTo(this, other) <= maxDistance;
    }

    // 简单解析 a.b[0].c → ["a", "b", "c"]
    private List<String> parse(String path) {
        return Arrays.stream(path.split("[.\\[\\]]"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(VariablePath other) {
        int size = Math.min(this.segments.size(), other.segments.size());
        for (int i = 0; i < size; i++) {
            int cmp = this.segments.get(i).compareTo(other.segments.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(this.segments.size(), other.segments.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariablePath)) return false;
        VariablePath that = (VariablePath) o;
        return Objects.equals(this.segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }
}
