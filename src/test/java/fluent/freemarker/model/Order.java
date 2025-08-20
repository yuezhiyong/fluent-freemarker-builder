package fluent.freemarker.model;


import lombok.Data;

@Data
public class Order {
    private String orderNo;


    public Order(String orderNo) {
        this.orderNo = orderNo;
    }
}
