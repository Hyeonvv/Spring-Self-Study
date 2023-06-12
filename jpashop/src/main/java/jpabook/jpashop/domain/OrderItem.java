package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 주문 아이템 가격
    private int orderPrice;

    // 주문 수량
    private int count;

    // 현재 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할만을 하고 있다.
    // 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 <도메인 모델 패턴> 이라고 한다. -> 유지보수 ↑
    // 엔티티 내에 비즈니스 로직을 구현해놓았기 때문에, 이후로도 새로운 로직이 있을 떄 동일한 방식으로 구현하는것이 좋다.
    // 그렇기 때문에 Service 에서 새로 빈 객체를 생성하여 set 을 통해 비즈니스 로직을 구현하지 못하도록
    // protected 생성자를 통해 빈 객체 생성을 강제로 막아놓을 수 있다.
    // 이를 @NoArgsConstructor(access = AccessLevel.PROTECTED) 어노테이션을 통해 간단히 처리할 수 있다.

//    protected OrderItem() {
//    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    /**
     * 취소한 아이템의 재고 수량 원복
     */
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
