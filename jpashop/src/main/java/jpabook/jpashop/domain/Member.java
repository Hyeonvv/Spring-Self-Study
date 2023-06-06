package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") // 초기 테이블 분석 시 MEMBER_ID 로 설정해두었기 때문에 별도 설정
    private Long id;

    private String name;

    @Embedded // 내장 타입을 포함했다. 어노테이션은 @Embedded 나 @Embeddable 둘 중 하나만 있어도 동작한다. 이해를 위해 둘 다 작성
    private Address address;

    // 1 : N (하나의 회원이 여러 개의 주문 가능)
    @OneToMany(mappedBy = "member") // mappedBy -> '나는 Order 테이블의 member 필드에 의해서 매핑되었다, 거울이다' 라는 뜻.
    private List<Order> orders = new ArrayList<>();

}
