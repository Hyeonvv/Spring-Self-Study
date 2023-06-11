package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders") // 테이블 명 order 로 변경, 예약어 때문에 관례상 orders 를 많이 사용한다.
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // N : 1, @XToOne 관계는 기본 fetchType: EAGER(즉시로딩) -> 직접 LAZY(지연로딩) 으로 바꿔줘야 한다.
    @JoinColumn(name = "member_id") // 매핑을 뭐로 할거냐 -> member_id : foreign key
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // @XToMany 관계는 기본 fetchType: LAZY(지연로딩) -> 따로 설정해줄 필요 X
    private List<OrderItem> orderItems = new ArrayList<>();

    // 1:1 관계 설정 시 외래키는 두고 싶은 곳에 둬도 된다. 보통 Access 를 많이 하는 곳에 설정한다.
    // Delivery 보다는 접근이 많을 것 같은 Order 쪽에 외래키를 설정하기로 했다.
    // cascadeType.ALL -> 원래는 delivery, Order 둘 다 persist 해 줘야 하는데, delivery 값을 세팅하고 Order만 persist 해 줘도 둘 다 persist 가 된다.
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 주문 시간, Date 를 쓰고 어노테이션을 지정해줬을 때와 달리 LocalDateTime 을 쓸 시 Hibernate 가 알아서 처리해준다.
    private LocalDateTime orderDate;

    // 주문 상태 [ORDER, CANCEL]
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * 연관관계 편의 메서드 : 양방향일 때 양쪽 세팅을 한 메서드로 해결
     * 위치는 핵심적으로 control 하는 쪽이 들고있는게 좋다.
     */
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    // OrderItem... -> 0개 이상의 OrderItem 객체를 인자로 받겠다.
    // 주문 생성 로직을 한곳에 응집 -> 나중에 주문 생성 관련 로직을 수정하려면 여기만 수정하면 된다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem: orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setDelivery(delivery);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: this.orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem: orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
