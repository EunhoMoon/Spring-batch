package study.springbatch.core.domain.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import study.springbatch.core.domain.orders.Orders;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Entity
@ToString
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue
    private Long id;
    private String orderItem;
    private Integer price;
    private LocalDate orderDate;
    private LocalDate accountDate;

    public Accounts(Orders orders) {
        this.id = orders.getId();
        this.orderItem = orders.getOrderItem();
        this.price = orders.getPrice();
        this.orderDate = orders.getOrderDate();
        this.accountDate = LocalDate.now();
    }
}
