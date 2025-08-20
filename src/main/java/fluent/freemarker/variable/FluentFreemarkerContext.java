package fluent.freemarker.variable;

import fluent.freemarker.model.TypeRegistry;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
public class FluentFreemarkerContext {
    private final Map<String, Object> context;
    private final VariableRegistry variableRegistry;
    private final TypeRegistry typeRegistry;

    // Private constructor for internal use
    private FluentFreemarkerContext() {
        this.context = new HashMap<>();
        this.variableRegistry = new VariableRegistry();
        this.typeRegistry = new TypeRegistry();
    }

    /**
     * Start building a new context.
     */
    public static FluentFreemarkerContext create() {
        return new FluentFreemarkerContext();
    }

    /**
     * Add a key-value pair to the context.
     */
    public FluentFreemarkerContext with(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public FluentFreemarkerContext var(String path, Object value) {
        context.put(path, value);
        getVariableRegistry().register(path);
        log.trace("Registered variable path: {}", path);
        return this;
    }

    // 批量注册
    public FluentFreemarkerContext vars(Map<String, Object> map) {
        map.forEach(this::var);
        return this;
    }



    /**
     * Add only if value is non-null.
     */
    public FluentFreemarkerContext withIfPresent(String key, Object value) {
        if (value != null) {
            context.put(key, value);
        }
        return this;
    }

    /**
     * Add using Optional.
     */
    public FluentFreemarkerContext withOptional(String key, Optional<?> optional) {
        optional.ifPresent(value -> context.put(key, value));
        return this;
    }

    /**
     * Add multiple entries at once.
     */
    public FluentFreemarkerContext withAll(Map<String, ?> map) {
        context.putAll(map);
        return this;
    }

    /**
     * Add using a consumer (for complex logic).
     */
    public FluentFreemarkerContext with(Consumer<Map<String, Object>> customizer) {
        customizer.accept(context);
        return this;
    }

    /**
     * Build and return the raw context map.
     */
    public Map<String, Object> build() {
        return new HashMap<>(context); // Return immutable copy
    }

    /**
     * Add a stream as a list to the context (collects stream safely).
     * If stream is null, an empty list is used.
     */
    public FluentFreemarkerContext withCollection(String key, Stream<?> stream) {
        try {
            if (stream == null) {
                context.put(key, new ArrayList<>()); // Java 9+ empty immutable list
                log.trace("Added empty list for key '{}' (stream was null)", key);
            } else {
                // Collect to avoid "stream has already been operated upon" error
                List<?> collected = stream.collect(Collectors.toList());
                context.put(key, collected);
                log.trace("Added collection with {} items for key '{}'", collected.size(), key);
            }
        } catch (Exception e) {
            log.error("Failed to collect stream for key: {}", key, e);
            context.put(key, new ArrayList<>()); // fallback
        }
        return this;
    }


    public FluentFreemarkerContext withArray(String key, Object... array) {
        context.put(key, array != null ? Arrays.asList(array) : Collections.emptyList());
        log.trace("Added array as list with {} items for key '{}'", array == null ? 0 : array.length, key);
        return this;
    }


    /**
     * Add a filtered collection to the context.
     * Filters the given stream with the predicate and collects the result into a list.
     * Safe against null stream or null predicate.
     *
     * @param key    the key to store the filtered list
     * @param stream the input stream (can be null)
     * @param filter the filter predicate (can be null, treated as "allow all")
     * @return this builder
     */
    public FluentFreemarkerContext withFilteredCollection(
            String key,
            Stream<?> stream,
            Predicate<?> filter) {

        try {
            List<?> result;

            if (stream == null) {
                result = Collections.emptyList();
                log.trace("Stream is null for key '{}'. Using empty list.", key);
            } else {
                @SuppressWarnings("unchecked")
                Predicate<Object> typeSafeFilter = filter != null
                        ? (Predicate<Object>) filter
                        : obj -> true; // allow all if predicate is null

                result = stream
                        .filter(typeSafeFilter)
                        .collect(Collectors.toList());

                log.trace("Filtered stream for key '{}': {} items matched", key, result.size());
            }

            context.put(key, new ArrayList<>(result)); // mutable copy
        } catch (Exception e) {
            log.error("Failed to process filtered collection for key:{} ", key, e);
            context.put(key, Collections.emptyList());
        }

        return this;
    }


    /**
     * Add a sorted collection to the context.
     * Collects the stream into a list, sorted by the given comparator.
     * If stream is null, an empty list is used.
     * If comparator is null, natural order is used (if elements are Comparable).
     *
     * @param key        the key to store the sorted list
     * @param stream     the input stream (can be null)
     * @param comparator the comparator to sort elements (can be null)
     * @return this builder
     */
    public FluentFreemarkerContext withSortedCollection(
            String key,
            Stream<?> stream,
            Comparator<?> comparator) {

        try {
            List<?> result;

            if (stream == null) {
                result = Collections.emptyList();
                log.trace("Stream is null for key '{}'. Using empty list.", key);
            } else {
                @SuppressWarnings("unchecked")
                Comparator<Object> typeSafeComparator = comparator != null
                        ? (Comparator<Object>) comparator
                        : (o1, o2) -> {
                    if (o1 instanceof Comparable && o2 != null) {
                        try {
                            return ((Comparable<Object>) o1).compareTo(o2);
                        } catch (ClassCastException e) {
                            log.debug("Elements are not naturally comparable: {} and {}", o1.getClass(), o2.getClass());
                            return 0;
                        }
                    }
                    return 0; // cannot compare
                };
                result = stream
                        .collect(Collectors.toList()) // collect first to avoid stream reuse
                        .stream()
                        .sorted(typeSafeComparator)
                        .collect(Collectors.toList());
                log.trace("Sorted collection for key '{}': {} items sorted.", key, result.size());
            }
            context.put(key, new ArrayList<>(result));
        } catch (Exception e) {
            log.error("Failed to sort collection for key:{} ", key, e);
            context.put(key, Collections.emptyList());
        }
        return this;
    }

    /**
     * Render template from string.
     */
    public String render(String templateContent) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        Template template = new Template("inline", templateContent, cfg);
        try (Writer out = new StringWriter()) {
            template.process(build(), out);
            return out.toString();
        }
    }

    public <T> FluentFreemarkerContext withFilteredCollection(
            String key,
            Collection<T> collection,
            Predicate<? super T> filter) {
        if (collection == null || collection.isEmpty()) {
            context.put(key, Collections.emptyList());
            return this;
        }
        return withFilteredCollection(key, collection.stream(), filter);
    }

    public <T> FluentFreemarkerContext withSortedCollection(
            String key,
            Collection<T> collection,
            Comparator<? super T> comparator) {
        if (collection == null || collection.isEmpty()) {
            context.put(key, Collections.emptyList());
            return this;
        }
        return withSortedCollection(key, collection.stream(), comparator);
    }


    public <T, K> FluentFreemarkerContext withGroupedBy(
            String key,
            Stream<T> stream,
            Function<? super T, ? extends K> classifier) {

        try {
            Map<K, List<T>> result;

            if (stream == null) {
                result = Collections.emptyMap();
            } else {
                result = stream
                        .collect(Collectors.groupingBy(
                                classifier,
                                HashMap::new,
                                Collectors.toList()
                        ));
            }
            log.trace("Grouped stream for key '{}': {} groups formed.", key, result.size());
            context.put(key, new HashMap<>(result)); // 安全协变
        } catch (Exception e) {
            log.error("Failed to group stream for key:{}", key, e);
            context.put(key, Collections.emptyMap());
        }
        return this;
    }

    /**
     * Render template from file (optional)
     */
    public String renderFromFile(String templatePath) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(FluentFreemarkerContext.class, "/");
        cfg.setDefaultEncoding("UTF-8");

        Template template = cfg.getTemplate(templatePath);
        try (Writer out = new StringWriter()) {
            template.process(build(), out);
            return out.toString();
        }
    }

    @Override
    public String toString() {
        return "FluentContext{" + "context=" + context + '}';
    }
}
