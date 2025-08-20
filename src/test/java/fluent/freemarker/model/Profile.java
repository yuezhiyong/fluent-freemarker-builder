package fluent.freemarker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Profile {
    private String role;
    private String department;
    private String phone;

    // 构造函数
    public Profile(String role, String department) {
        this.role = role;
        this.department = department;
    }

    public Profile(String role, String department, String phone) {
        this.role = role;
        this.department = department;
        this.phone = phone;
    }


    @Override
    public String toString() {
        return "Profile{" +
                "role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
