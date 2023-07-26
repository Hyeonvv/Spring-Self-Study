package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.swing.text.html.parser.Entity;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*; // SubQuery
import static org.assertj.core.api.Assertions.*; // Junit Test
import static study.querydsl.entity.QMember.*; // QMember
import static study.querydsl.entity.QTeam.*; // QTeam

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

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 오름차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        // nullsLast(), nullsFirst(): 해당 필드의 값이 null 인 행이 있을 경우 어디에 위치시킬지 지정
        // nullsLast(): 제일 마지막에 위치시킨다.
        // nullsFirst(): 제일 처음에 위치시킨다.
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0); // username 오름차순 정렬이기 때문에 member5 먼저,
        Member member6 = result.get(1); // 그리고 member6 순서로 나온다.
        Member memberNull = result.get(2); // nullsLast() 때문에 마지막에 위치

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isEqualTo(null);
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 시작 지점: 1개 건너뛴 first index 부터.  * offset(0): zero index 부터(처음부터)
                .limit(2) // 페이지 사이즈: 최대 2건 조회
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 0 부터 시작(zero index부터)
                .limit(2) // 최대 2건 조회
                .fetchResults();

        // getTotal() 메소드는 쿼리 결과의 총 항목 수를 반환한다.
        // 이는 offset 이나 limit 설정과 무관하게, 주어진 쿼리에 대해 일치하는 전체 결과의 수를 나타낸다.
        // selectFrom(member).orderBy(member.username.desc()) 조건에 해당하는 전체 회원 수가 4명
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2); // getLimit(): 쿼리 결과의 최대 개수
        assertThat(queryResults.getOffset()).isEqualTo(1); // getOffset(): 몇번째 index 부터 데이터를 가져오는지
        assertThat(queryResults.getResults().size()).isEqualTo(2); // 뽑아온 데이터의 size(개수)
    }

    /**
     * JPQL
     * select
     *      COUNT(m), // 회원 수
     *      SUM(m.age), // 나이 합
     *      AVG(m.age), // 평균 나이
     *      MAX(m.age), // 최대 나이
     *      MIN(m.age) // 최소 나이
     * from Member m
     */
    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(
                        member.count(), // 수
                        member.age.sum(),
                        member.age.avg(),
                        member.age.min(),
                        member.age.max()
                )
                .from(member)
                .fetch();
        // Tuple: queryDSL 에서 제공하는 기능
        // 데이터 타입이 여러 개가 들어올 때 사용한다.
        // 실무에서는 DTO 로 직접 뽑아오는 방법을 더 많이 사용한다.
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     *
     * JPQL
     * select
     *     team.name,
     *     avg(member1.age)
     * from
     *     Member member1
     * inner join
     *     member1.team as team
     * group by
     *     team.name
     */
    @Test
    public void group() throws Exception {

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg()) // 각 팀의 이름과, 그 팀의 회원들의 나이 평균을 가져와라.
                .from(member)
                .join(member.team, team) // 멤버가 속한 팀(member.team) 의 alias = team
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15); // (10 + 20) / 2

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35); // (30 + 40) / 2
    }

    /**
     * 팀 A 에 소속된 모든 회원
     * f
     */
    @Test
    public void join() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) // team -> Qteam.team 을 얘기한다.
                .where(team.name.eq("teamA")) // team 이름이 teamA 인 것들
                .fetch();

        assertThat(result)
                .extracting("username") // result 리스트의 각 Member 객체에서 username 필드 추출
                .containsExactly("member1", "member2"); // 추출된 사용자 이름이 member1과 member2 를 포함하는지 확인
    }

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)(카테시안 곱)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     * from 절에 여러 엔티티 선택해서 세타 조인 -> 모든 회원과 모든 팀을 가져온 다음 다 조인한 후 where 절에서 필터링
     * DB 가 알아서 성능 최적화 작업을 해준다.
     * > 외부 조인은 불가능하지만(left join, right join ...) 할 수 있는 방법이 따로 있다고 한다.
     */
    @Test
    public void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA 인 팀만 조인, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     * -> 멤버는 전부 조회해오고, 팀은 teamA에 해당하는 팀만 조회된다. teamA가 아닌 멤버들의 팀 반환값은 null.
     * 내부조인(inner join) 의 경우에는 where 절을 넣는거랑 기능이 동일하다. -> 내부조인 때는 where 절로 해결하는 것이 좋다.
     */
    @Test
    public void join_on_filtering() {
        List<Tuple> result = queryFactory
                .select(member, team) // select 절이 여러 가지 타입이기 때문에 반환값은 Tuple 로 나온다.
                .from(member)
                .leftJoin(member.team, team)
//                .innerJoin(memberr.team, team)
                .on(team.name.eq("teamA"))
//                .where(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     * 기존의 일반 조인과 문법이 조금 다르다.
     * 일반 조인: leftJoin(member.team, team)
     * on 조인: leftJoin(team)
     */
    @Test
    public void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());// 로딩된 엔티티인지 아닌지 알려준다.
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse() {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin() // 멤버를 조회할 때 연관된 팀을 한번에 끌어온다. LAZY 로딩이 걸려있는 엔티티 같이 조회 가능
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());// 로딩된 엔티티인지 아닌지 알려준다.
        assertThat(loaded).as("페치 조인 미적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() throws Exception {

        // 서브쿼리는 alias 가 달라야 하기 때문에 새로운 QMember 생성
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    /**
     * 나이가 평균 이상인 회원
     * goe(>=): greater or equal
     */
    @Test
    public void subQueryGoe() throws Exception {

        // 서브쿼리는 alias 가 달라야 하기 때문에 새로운 QMember 생성
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe( // >=
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }

    /**
     * 멤버 중 (멤버 나이가 10 보다 많은 멤버) 에 속하는 인원 조회
     * in
     * gt(>): greater than
     */
    @Test
    public void subQueryIn() throws Exception {

        // 서브쿼리는 alias 가 달라야 하기 때문에 새로운 QMember 생성
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }

    /**
     * select 절에서의 서브쿼리 사용 예제
     * 멤버 이름 | 전체 멤버들의 평균 나이
     * member1 | 25
     * member2 | 25
     * member3 | 25
     * member4 | 25
     */
    @Test
    public void selectSubQuery() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * > JPA JPQL 서브쿼리의 한계
     * from 절의 서브쿼리는 지원하지 않는다.
     * 당연히 QueryDsl 또한 from 절의 서브쿼리를 지원하지 않는다.
     * (QueryDsl 은 JPQL 빌더 역할을 하는 것이기 떄문에 JPQL 에서 안되면 QueryDsl 에서도 불가능하다.)
     * JPA 표준 스펙에서는 select 절에서도 서브쿼리를 지원하지 않지만, Hibernate 구현체를 사용하면 select 절에서는 사용이 가능하다.
     * <p>
     * <p>
     * > from 절의 서브쿼리 해결 방안 (Hibernate 나중 버전에서 해결이 될 수도 있다고 한다.)
     * 1. 서브쿼리를 join 으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
     * 2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다. (엄청 긴 양의 한줄의 쿼리보다 두 개로 나누는 것이 더 나을수도 있다.)
     * 3. nativeSQL 을 사용한다.
     */

    @Test
    public void basicCase() throws Exception {

        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")) // else
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() throws Exception {

        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0 ~ 20 살")
                        .when(member.age.between(21, 30)).then("21 ~ 30 살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 임의의 순서로 회원을 출력하고 싶다면? 
     * 0 ~ 30 살이 아닌 회원을 가장 먼저 출력
     * 0 ~ 20 살 회원 출력
     * 21 ~ 30 살 회원 출력
     *
     * rankPath 에서 case 문을 통해 순서 설정한 뒤 orderBy 이용하여 내림차순 나열
     */
    @Test
    public void orderByWithCase() throws Exception {

        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc()) // 내림차순 나열
                .fetch();

        for (Tuple tuple : result) {
//            String username = tuple.get(member.username);
//            Integer age = tuple.get(member.age);
//            Integer rank = tuple.get(rankPath);
//            System.out.println("username = " + username + " age = " + age + " rank = " + rank);
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 조회 목록에 상수가 필요하면 Expressions.constant() 를 사용할 수 있다.
     * JPQL 에서는 표시되지 않는다.
     */
    @Test
    public void constant() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void concat() throws Exception {

        // {username}_{age} 처럼 표현하고 싶을 때
        // 같은 타입이 아니면 concat 이 되지 않기 때문에 member.age.stringValue() 처럼 작성
        // * stringValue() 는 실무에서 사용할 일이 많다.(ENUM 을 사용할 때 자주 사용)
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * 프로젝션 : select 절에 뭘 가져올 지 대상을 지정하는 것
     * 프로젝션 대상이 하나면 반환 타입을 명확하게 지정할 수 있다.
     * 프로젝션 대상이 둘 이상이면 Tuple 이나 DTO 로 조회한다.
     */
    @Test
    public void simpleProjection() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * Tuple 은 실무에서 Repository 계층 안에서만 사용하고 반환할 때는 DTO 로 바꿔서 반환하는 것이 좋다.
     * -> Tuple 은 QueryDsl 에 종속적인 기능이기 때문에 의존성을 최소화하는 것이 좋다.
     * -> 나중에 만약 QueryDsl 에서 다른 기술로 바꾸더라도 Repository 쪽만 수정하면 되기 때문이다. (Controller, Service 등 건드릴 필요가 없음.)
     *
     */
    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);

            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    /**
     * 순수 JPA 에서 DTO 조회 -> 잘 사용하지 않음
     * new Operation 을 활용하는 방법
     * DTO 의 package 이름을 다 적어줘야해서 지저분하다.
     * 생성자 방식만 지원한다.(setter 같은것들 지원하지 않음)
     */
    @Test
    public void findDtoByJPQL() throws Exception {

        // 마치 생성자를 만드는 것처럼 작성 -> MemberDto 의 생성자를 통해 값이 넘어온다.
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * Setter 를 활용한 프로퍼티 접근 방법
     */
    @Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, // 타입 지정
                        member.username, // 꺼내올 값
                        member.age)) // 꺼내올 값
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 필드 직접 접근 방법
     * Setter 가 필요 없다. 필드에 선언되어 있는 변수들 만으로도 가능하다.
     */
    @Test
    public void findDtoByField() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class, // 타입 지정
                        member.username, // 꺼내올 값
                        member.age)) // 꺼내올 값
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 생성자 사용 방법
     */
    @Test
    public void findDtoByConstructor() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, // 타입 지정
                        member.username, // 꺼내올 값
                        member.age)) // 꺼내올 값
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 프로퍼티, 필드 접근 생성 방식에서
     * DTO 필드 선언 변수명과 엔티티 변수명이 다른 경우 해결 방안
     */
    @Test
    public void findUserDtoByField() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"), // alias 를 통해 명을 맞춰준다.
                        ExpressionUtils.as(JPAExpressions // 서브쿼리를 사용할 때 alias 명 설정하는 방법
                                .select(memberSub.age.max())
                                        .from(memberSub), "age")))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    /**
     * 생성자 + @QueryProjection 활용
     * @QueryProjection 어노테이션을 달아준 후 compileQuerydsl -> DTO 의 Q 타입이 생성된다.
     * 컴파일러로 타입 체크할 수 있다.
     * 다만 DTO 에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO 까지 Q 타입 파일을 생성해야 하는 단점이 있다.
     * DTO 가 @QueryProjection 주석 때문에 QueryDSL 에 의존하는 상태, 순수하지 않은 상태
     *
     * 의존성 없이 순수하게 DTO 를 사용하고 싶은 경우에는 다른 방법을 사용하는 것이 좋고,
     * 실용적인 관점에서 QueryDSL 을 많이 사용, 의존하고 하부 기술들도 별로 바뀔 것 같지 않은 경우에는 이 방법을 사용하면 좋다.
     */
    @Test
    public void findDtoByQueryProjection() throws Exception {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age)) // 작성 시점에 타입 체크 가능 -> 가장 안전한 방법
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    /**
     * 동적 쿼리를 해결하는 두 가지 방식
     * - BooleanBuilder ✔
     * - Where 다중 파라미터 사용
     */
    @Test
    public void dynamicQuery_BooleanBuilder() throws Exception {
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    /**
     * 파라미터의 값이 null 이냐 아니냐에 따라 쿼리가 동적으로 바뀌어야 한다.
     */
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder(); // 기본 초기 조건을 넣을수도 있다.
//        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameCond)); // 기본 초기 조건을 넣은 경우
        
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }

        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();

        return result;
    }


}
