package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

// * 테스트를 먼저 작성하고 구현체를 만드는 것 -> 테스트 주도 개발 (TDD)
// 테스트를 반드시 작성하는 습관을 가져야 한다.
public class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    // AfterEach: 각 메소드가 실행이 끝날 때마다 어떠한 동작을 하게끔 하는 기능
    // 테스트는 서로 순서와 관계 없이, 의존 관계 없이 설계가 되어야 한다. -> 하나의 테스트가 끝날 때마다 저장소나, 공용 데이터들을 지워줘야 한다.
    @AfterEach
    public void afterEach() {
        repository.clearStore(); // 테스트가 끝날 때마다 저장소를 지워준다(클리어).
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();

//        junit Assertions
//        test를 위한 기능, member 와 result 가 같다면 아무것도 뜨지 않지만 테스트가 성공적으로 진행된다. 다르다면 빨간 불(에러) 발생
//        Assertions.assertEquals(member, result);

//        assertj Assertions(좀 더 편하게 사용 가능)
//        실무에서는 테스트가 통과되지 않으면 다음 단계로 넘어가지 못하게 막아버린다.
        assertThat(member).isEqualTo(result); // 기대한 값: result, 실제 값: member
    }

    @Test
    public void findByName() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        Member result = repository.findByName("spring1").get();

        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll() {
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring1");
        repository.save(member2);

        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }
}
