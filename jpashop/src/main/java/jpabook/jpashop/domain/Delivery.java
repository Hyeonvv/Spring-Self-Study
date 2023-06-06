package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    // READY, COMP
    // 타입은 꼭 STRING 으로 쓰는 것이 좋다 순서 관련 X
    // * ORDINAL -> 순서에 영향 받는 숫자 형식
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
