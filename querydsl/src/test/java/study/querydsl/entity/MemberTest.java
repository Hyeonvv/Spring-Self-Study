package study.querydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void testEntity() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush(); // 영속성 컨텍스트에 있는 데이터들을 쿼리 만들어서 실제 DB 에 날린다.
        em.clear(); // 영속성 컨텍스트를 완전히 초기화, 캐시 다 날라감.

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        // 실제 테스트짤때는 system.out 쓰지말고 assert 사용해야 한다.
        for (Member member : members) {
            System.out.println("member: " + member);
            System.out.println("-> member.team: " + member.getTeam());
        }
    }
}