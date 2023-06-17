package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // Controller 에서 createMemberForm 화면으로 이동할 때 memberForm 이라는 껍데기 객체를 가지고 이동한다.
    @GetMapping("members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    // @Valid: 유효성 검사 -> MemberForm 에 있는 @NotEmpty 인식
    // BindingResult: 유효성 검사(@Valid) 이후 발생한 오류 감지
    @PostMapping("members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) { // 오류가 존재할 시 다시 createMemberForm 보여줌
            return "/members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
}
