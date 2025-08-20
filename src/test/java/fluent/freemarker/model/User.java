package fluent.freemarker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private int age;
    private String email;

    public User(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }


}
