package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // 스프링과 테스트를 통합해서 실제 DB 까지 다 엮어서 돌려보겠다.
@SpringBootTest
@Transactional // Rollback 을 위한 Transactional : Test 가 반복되어 실행될 수 있게끔 하는 역할
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        // Test 에선 Transactional 사용 시 마지막에 Rollback 이 되기 떄문에 insert 문이 나가지 않는다.
        // insert 문이 나가는거까지 확인해보고 싶다면 em.flush() 를 통해서나 Rollback(false) 어노테이션을 통해 확인할 수 있다.
        // em.flush랑 Transactional 둘 다 썼을 때 -> insert 쿼리 나간 후 Transactional 때문에 Rollback 실행됨
        // Rollback(false)만 썼을 때 -> insert 나간 후 Rollback 이 안되기 때문에 실제 DB에 값이 들어간다.
//        em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class) // 해당 테스트 메서드가 IllegalStateException 예외를 던져야 정상적으로 테스트가 통과한다는 것을 의미
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");
        //when
        memberService.join(member1);
        memberService.join(member2); // 여기서 예외가 터져야 한다.
//        Test(expected = IllegalStateException.class) 를 통해서 코드를 줄일 수 있다.
//        try {
//            memberService.join(member2);
//        } catch (IllegalStateException e) { // 터진 예외 catch
//            return;
//        }
        //then
        // 여기까지 오면 안된다. 를 확인하기 위한 Junit 의 fail 코드
        // 이 부분에 도달했다는 것은 예외가 발생하지 않았으므로 테스트가 실패했다는 것을 의미한다.
        fail("예외가 발생해야 한다.");
    }
}