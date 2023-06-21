package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    // 필드에 가져와도 괜찮다.
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        // JPAQueryFactory 를 만들 때 entityManager 를 넘겨줘야 JPAQueryFactor 가 entityManager 를 이용해서 데이터를 찾아올 수 있다.
        queryFactory = new JPAQueryFactory(em);

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
    }

    /**
     * JPQL 단점
     * 문자열로 작성하기 때문에 오타가 나도 쉽게 찾을 수 없다.
     * 실행해보고 나서야 오류가 있음을 확인할 수 있다.
     * 파라미터 바인딩이 Querydsl 에 비해 귀찮음.
     */
    @Test
    public void startJPQL() {
        // member1을 찾아라

        String qlString =
                "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * Querydsl 의 장점
     * 자바 코드로 작성하기 때문에 작성 도중에도 언제든지 오류를 발견할 수 있다.
     * 파라미터 바인딩을 자동으로 해결해 준다.
     * IDE 의 자동 완성 기능을 통해서도 도움을 받을 수도 있다.
     */
    @Test
    public void startQuerydsl() {
        // member1을 찾아라

        // new QMember("m") 의 "m" -> 어떤 QMember 인지를 구분하는 용도 (크게 중요하지는 않다, 어차피 쓰지 않는다)
        // 방법 1
//        QMember m = new QMember("m");
        // 방법 2 (QMember 내에 작성되어 있는 static 생성자 이용)
//        QMember m = Qmember.member;
        // 방법 3 (권장 방법)
        // static import

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // parameter 바인딩 없이도 이렇게 구현이 가능하다.
                .fetchOne(); // 결과값을 하나만 가져올 때 사용, 쿼리의 결과가 없을 땐 null 반환, 두개 이상의 결과가 있을 경우 NonUniqueResultException 발생

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member) // select(member) + from(member) 을 합칠 수 있다.
                .where(member.username.eq("member1")
                        .and(member.age.eq(10))) // and 나 or 로 조건을 걸 수 있다.
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"), // 쉼표를 통해 and 와 같은 효과를 낼 수 있다.
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void resultFetch() {
        // 리스트 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단건 조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        // limit(1).fetchOne() 과 같은 기능
        // 쿼리 결과의 첫번째 레코드만 반환한다.
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징에서 사용
        // fetchResults() -> 두번의 쿼리가 실행된다.
        // 1. 전체 결과 수를 가져오는 카운트 쿼리 -> getTotal() 을 통해 확인 가능
        // 2. 실제 데이터를 가져오는 쿼리 -> getResults() 을 통해 확인 가능
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        // Count 만 뽑아오는 기능
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }
}
