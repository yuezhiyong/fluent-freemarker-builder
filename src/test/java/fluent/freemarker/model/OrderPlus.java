package fluent.freemarker.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderPlus {

    private String id;

    private ProfilePlus profile;


    private List<Item> items;
}
