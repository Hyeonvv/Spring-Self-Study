package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // @Controller 어노테이션이 있으면 스프링 컨테이너에서 스프링 Bean이 관리된다.
public class MemberController {

    private final MemberService memberService;

    // 생성자에 @Autowired 가 붙어있으면 스프링이 컨테이너에서 관리되고 있는 연관된 객체를 가져온다.
    // 이렇게 객체 의존관계를 외부에서 넣어주는 것을 DI(Dependency Injection): 의존성 주입 이라고 한다
    // Bean 이란? : 스프링 컨테이너에 의해 관리되는 객체를 의미
    // Bean 을 정의하려면, 일반적으로 @Component(@Service, @Repository, @Controller) 어노테이션을 사용하여 해당 클래스를 Bean 으로 등록
    // * @Service, @Repository, @Controller 어노테이션은 기본적으로 @Component 를 포함하고 있다.
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get 방식으로 페이지 조회
     */
    @GetMapping("members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    /**
     * Post 방식으로 새로운 member 저장
     */
    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/"; // 회원가입이 끝났으니 홈 화면으로 redirect(이동)
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }
}
