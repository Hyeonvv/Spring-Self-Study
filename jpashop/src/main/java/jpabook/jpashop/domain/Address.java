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
     * Java 에서는 클래스가 어떠한 생성자도 정의하지 않을 경우 컴파일러가 자동으로 기본 생성자를 추가한다.
     * 하지만 클래스에서 이미 매개변수가 있는 생성자를 정의하면, 컴파일러는 더 이상 기본 생성자를 추가하지 않는다.
     * 현재 public Address(String city, String street, String zipcode) 생성자를 정의하였기 때문에 컴파일러는 기본 생성자를 자동으로 추가하지 않는다.
     * JPA 에서는 엔티티가 있거나, Embeddable 클래스를 사용할 때, 리플렉션을 통해 객체를 생성하는데 이 때 매개변수가 없는 기본 생성자가 필요하다.
     * public 보다는 protected 로 하는것이 좋다.
     *
     * 리플렉션 : 리플렉션(Reflection)은 자바에서 객체를 통해 클래스의 정보를 분석해 내는 프로그램 기법을 말한다.
     * 클래스의 이름, 필드(field), 메소드(method), 생성자(constructor) 등의 정보를 알아내거나, 그것을 이용해 새로운 객체를 생성하거나, 메소드를 호출하거나 하는 일을 할 수 있다.
     */
    protected Address() {

    }

    /**
     * Address 처럼 값 타입의 경우 Setter 를 사용하지 않고, 생성자에 값을 모두 초기화해서 변경 불가능한 클래스를 만드는 것이 좋다.
     */
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
