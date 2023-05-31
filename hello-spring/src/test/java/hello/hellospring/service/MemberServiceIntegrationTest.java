package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 스프링 컨테이너와 DB까지 연동해서 테스트 하는것을 통합 테스트라고 한다.
@SpringBootTest // 스프링 컨테이너와 테스트를 함께 실행한다.
@Transactional // Transactional 을 테스트에 사용 시 테스트 하나마다(메소드마다) 시작 전에 Transaction 을 실행하고, 테스트 실행 후 Rollback 한다. -> 다음 테스트에 영향 X
class MemberServiceIntegrationTest {

    // Test 할 때는 보통 필드 주입(Field Injection)을 통해 간편하게 사용한다.
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() {
        //given -> 뭔가가 주어지고
        Member member = new Member();
        member.setName("spring");

        //when -> 이것을 실행했을 때
        Long saveId = memberService.join(member);

        //then -> 이러한 결과 도출
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}