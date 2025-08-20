package fluent.freemarker.variable;

import java.util.*;
import java.util.stream.Collectors;

public class VariableRegistry {
    private final Set<VariablePath> registeredPaths = new HashSet<>();

    public VariableRegistry register(String path) {
        registeredPaths.add(new VariablePath(path));
        return this;
    }

    public VariableRegistry registerAll(String... paths) {
        Arrays.stream(paths).forEach(this::register);
        return this;
    }

    public boolean knows(String path) {
        return knows(new VariablePath(path));
    }

    public boolean knows(VariablePath path) {
        return registeredPaths.contains(path);
    }

    public List<VariablePath> findSuggestions(String partialPath) {
        VariablePath prefix = new VariablePath(partialPath);
        return registeredPaths.stream()
                .filter(p -> p.isSimilarTo(prefix, 3))
                .sorted(Comparator.comparing(VariablePath::toString))
                .collect(Collectors.toList());
    }

    public Set<VariablePath> getAllPaths() {
        return new HashSet<>(registeredPaths);
    }

    public boolean isRootRegistered(String varName) {
        return registeredPaths.stream()
                .anyMatch(p -> p.getSegments().get(0).equals(varName));
    }
}
