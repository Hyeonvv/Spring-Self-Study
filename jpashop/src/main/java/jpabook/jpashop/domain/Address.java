package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 어딘가에 내장이 될 수 있다.
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    /**
     * JPA 구현 라이브러리가 프록시같은 기술을 쓸 때 허용해야 되기 때문(?)
     */
    protected Address() {

    }

    /**
     * JP
     */
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
