package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.") // 이 값이 비어있으면 오류가 난다는 뜻, @Valid 와 함께 사용하여야 한다.
    private String name;

    // 이 세개 값들은 필수가 아님.
    private String city;
    private String street;
    private String zipcode;
}
