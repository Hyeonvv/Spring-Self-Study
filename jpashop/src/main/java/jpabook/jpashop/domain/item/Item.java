package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // SINGLE_TABLE 전략 -> 한 테이블에 다 몰아넣겠다.
@DiscriminatorColumn(name = "dtype") // 구분 지을때 사용? 한다.
@Getter @Setter // Setter 로 값을 변경하는 것이 아닌 필요 메소드에 의해서 값을 변경하는 것이 좋다.
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    // 엔티티 자체가 해결할 수 있는 것들은 엔티티 안에 비즈니스 로직을 넣는 것이 좋다. 객체 지향적이고, 응집도가 높음 -> 도메인 주도 설계
    // 현재 stockQuantity 는 Item 엔티티 내에 존재하기 때문에 Item 엔티티 내에 비즈니스 로직을 설계
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}
