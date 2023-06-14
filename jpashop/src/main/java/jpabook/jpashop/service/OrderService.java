package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        // ⭐cascadeType.ALL 의 역할
        // 원래는 delivery 정보와 orderItem 정보를 따로 따로 다 Repository.save() 를 해줘야 하는데,
        // orderRepository 만 save 해도 delivery 와 orderItem 이 다 저장이 되는 이유 -> cascadeType.ALL 옵션 때문
        // 다른 것들이 OrderItem, Delivery 를 참조할 수 없고, Order 만이 참조하는 상황
        // 즉, Lifecycle 에 대해서 서로 동일하게 관리를 할 때 cascadeType.ALL 옵션을 사용한다.
        // 만약 다른 엔티티도 OrderItem, Delivery 를 참조하고 있는데 cascadeType.ALL 옵션을 걸어버리면
        // Order 를 삭제할 때 다른 것들도 같이 지워질 수도 있기 때문이다.
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {

        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 취소
        // ⭐ JPA 의 엄청난 장점
        // 데이터만 이렇게 변경하면 JPA 가 알아서 바뀐 변경 포인트들을 Dirty Checking(변경 내역 감지) 를 통해 DB 에 update 쿼리가 날아간다.
        // 따로 Repository 를 통해 save 를 다시 해 줄 필요가 없다.
        order.cancel();
    }

    /**
     * 주문 검색
     */
/*
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }
*/
}
