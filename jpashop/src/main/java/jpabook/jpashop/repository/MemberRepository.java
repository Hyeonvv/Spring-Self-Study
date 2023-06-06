package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository // 스프링 빈으로 등록, JPA 예외를 스프링 기반 예외로 예외 변환
public class MemberRepository {

    @PersistenceContext // 스프링이 EntityManager 를 만들어서 Injection(주입) 해준다.
    private EntityManager em;

    public void save(Member member) {
        em.persist(member); // persist -> 영속성 context 에 멤버 객체 넣고, Transaction 커밋 시점에 DB에 반영(insert)
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // em.createQuery(JPQL, 반환 타입)
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // JPQL 의 name 과 바인딩
                .getResultList();
    }
}
