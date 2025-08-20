package fluent.freemarker;

import fluent.freemarker.builder.FTL;
import org.junit.jupiter.api.Test;

public class FTLTests {

    @Test
    public void testMethodChain(){
        String template = FTL.template()
                .append("Hello ").var("user.name").newline()
                .ifTrue("user.active", ifBlock -> {
                    ifBlock.append("Status: Active").newline();
                    ifBlock.list("user.orders", "o", "order", list -> {
                        list.append("Order ID: ").var("o.id")
                                .append(", Total: ").var("o.total")
                                .newline();
                    });
                })
                .ifElse("user.loggedIn",
                        ifBlock -> ifBlock.append("You are logged in.").newline(),
                        elseBlock -> elseBlock.append("Please log in.").newline()
                )
                .append("Thank you!")
                .validate()
                .toString();
        System.out.println(template);
    }
}
