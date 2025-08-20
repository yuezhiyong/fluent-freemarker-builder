package fluent.freemarker;

import fluent.freemarker.variable.VariablePath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class VariablePathTests {

    @Test
    public void testVariablePath() {
        VariablePath p1 = new VariablePath("user.name");
        VariablePath p2 = new VariablePath("user.name");
        VariablePath p3 = new VariablePath("user.email");

        // equals & hashCode
        Assertions.assertEquals(p1, p2);
        Assertions.assertEquals(p1.hashCode(), p2.hashCode());
        Assertions.assertEquals(p1, p3);

        // toString
        Assertions.assertEquals(p1.toString(), "user.name");

        // startsWith
        Assertions.assertTrue(p1.startsWith(new VariablePath("user")));
        Assertions.assertTrue(p1.startsWith(new VariablePath("user.name")));
        Assertions.assertFalse(p1.startsWith(new VariablePath("user.profile")));

        // compareTo
        List<VariablePath> list = Arrays.asList(p3, p1);
        list.sort(VariablePath::compareTo);
        Assertions.assertTrue(list.containsAll(Arrays.asList(p1, p3))); // email < name
    }
}
