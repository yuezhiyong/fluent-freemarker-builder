package fluent.freemarker;

import fluent.freemarker.variable.FluentFreemarkerContext;
import fluent.freemarker.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class FluentContextTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(FluentFreemarkerContext.class);

    @Test
    public void testUserFluent() {
        try {
            User user = new User("Alice", 30, "alice@example.com");
            Optional<String> otp = Optional.of("123456");
            String template = "Hello ${user.name},\n" + "Your age: ${user.age}\n" + "<#if otp??>Your OTP: ${otp}!</#if>\n" + "Items: <#list items as item>${item} </#list>";
            String result = FluentFreemarkerContext.create().with("user", user)
                    .withIfPresent("email", user.getEmail()).withOptional("otp", otp).with("items", Arrays.asList("Apple", "Banana", "Cherry")).with(custom -> {
                custom.put("timestamp", System.currentTimeMillis());
            }).render(template);
            System.out.println(result);
        } catch (Exception e) {
            LOGGER.error("出现错误:", e);
        }
    }


    @Test
    public void testBuildContext() {
        Map<String, Object> context = FluentFreemarkerContext.create().with("name", "Bob").withIfPresent("age", 25).withIfPresent("nullValue", null) // should be skipped
                .build();

        assertEquals("Bob", context.get("name"));
        assertEquals(25, context.get("age"));
        assertFalse(context.containsKey("nullValue"));
    }

    @Test
    public void testRender() throws Exception {
        String result = FluentFreemarkerContext.create().with("name", "Charlie").render("Hello ${name}!");
        assertEquals("Hello Charlie!", result);
    }


    @Test
    public void testStream() throws Exception {
        List<String> items = Arrays.asList("Apple", "Banana", "Cherry");
        String result = FluentFreemarkerContext.create().withCollection("fruits", items.stream().filter(s -> s.startsWith("A"))).render("<#list fruits as f>${f}</#list>");
        Assertions.assertEquals("Apple", result);
    }


    @Test
    public void testComplexStream() throws Exception {
        Stream<User> userStream = Stream.of(new User("Alice", 30, "a@example.com"), new User("Bob", 25, "b@example.com"));

        String template = "<#list users as u>${u.name} (${u.age}) </#list>";

        String result = FluentFreemarkerContext.create().withCollection("users", userStream).render(template);
        System.out.println(result);
    }


    @Test
    public void testUserFilter() throws Exception {
        List<User> users = Arrays.asList(new User("Alice", 32, "a@example.com"), new User("Bob", 28, "b@example.com"), new User("Charlie", 35, "c@example.com"));

        String template = " Senior users:\n" + "    <#list seniors as u>\n" + "    - ${u.name} (${u.age})\n" + "    </#list>";

        String result = FluentFreemarkerContext.create().withFilteredCollection("seniors", users.stream(), (Predicate<User>) user -> user.getAge() >= 30).render(template);

        System.out.println(result);
    }


    @Test
    public void testFilterCollection() throws Exception {
        List<User> users = Arrays.asList(new User("Alice", 32, "a@example.com"), new User("Bob", 28, "b@example.com"), new User("Charlie", 35, "c@example.com"));

        String res = FluentFreemarkerContext.create().with("title", "Active Users Report").withFilteredCollection("activeUsers", users.stream(), (Predicate<User>) u -> u.getAge() >= 30 && u.getEmail() != null).withCollection("tags", Stream.of("java", "ftl", "fluent").map(String::toUpperCase)).render("<h1>${title}</h1>\n" + "        <ul>\n" + "        <#list activeUsers as u>\n" + "          <li>${u.name} - ${u.email}</li>\n" + "        </#list>\n" + "        </ul>\n" + "        Tags: <#list tags as t>${t} </#list>");

        System.out.println(res);
    }


    @Test
    public void testSortedCollection() throws Exception {
        List<String> words = Arrays.asList("Apple", "Kiwi", "Banana", "Fig", "Strawberry");

        String result = FluentFreemarkerContext.create()
                .withSortedCollection(
                        "sortedWords",
                        words.stream(),
                        Comparator.comparing(String::length).thenComparing(String::compareTo)
                )
                .render("Words by length: <#list sortedWords as w>${w}(${w?length}) </#list>");

        System.out.println(result);
        // 输出：Fig(3) Kiwi(4) Apple(5) Banana(6) Strawberry(10)
    }


    @Test
    public void testFilterAndSort() throws Exception {
        // 改用 List，可多次创建 Stream
        List<User> users = Arrays.asList(
                new User("Alice", 30, "a@example.com"),
                new User("Bob", 25, "b@example.com"),
                new User("Charlie", 35, "c@example.com")
        );

        String res = FluentFreemarkerContext.create()
                .withFilteredCollection(
                        "adults",
                        users.stream(),  // 每次都从 list 创建新 stream
                        (Predicate<User>) u -> u.getAge() >= 30
                )
                .withSortedCollection(
                        "adultsSorted",
                        users.stream().filter(u -> u.getAge() >= 30),  // 新 stream
                        Comparator.comparing(User::getName)
                )
                .render("<h2>Adults (by name):</h2>\n" +
                        "            <ul>\n" +
                        "            <#list adultsSorted as u>\n" +
                        "              <li>${u.name} (${u.age})</li>\n" +
                        "            </#list>\n" +
                        "            </ul>");
        System.out.println(res);
    }


    @Test
    public void testGroupBy() throws Exception {
        List<User> users = Arrays.asList(
                new User("Alice", 25, "a@example.com"),
                new User("Bob", 32, "b@example.com"),
                new User("Charlie", 38, "c@example.com"),
                new User("Diana", 22, "d@example.com")
        );

        Function<User, String> ageGroup = user -> {
            int age = user.getAge();
            if (age < 30) return "Young";
            else if (age < 40) return "Middle";
            else return "Senior";
        };

        String template = "<#list usersByAgeGroup?keys as group>\n" +
                "      <h2>${group}</h2>\n" +
                "      <ul>\n" +
                "      <#list usersByAgeGroup[group] as u>\n" +
                "        <li>${u.name} (${u.age})</li>\n" +
                "      </#list>\n" +
                "      </ul>\n" +
                "    </#list>";

        String result = FluentFreemarkerContext.create()
                .withGroupedBy("usersByAgeGroup", users.stream(), ageGroup)
                .render(template);

        System.out.println(result);
    }
}
