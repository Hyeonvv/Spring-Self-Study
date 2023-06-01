package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JpaRepository를 extends 하고 있을 시 스프링에서 자동으로 Bean 에 등록을 해준다.
 */
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    /**
     * 규칙 존재 findBy___()
     * JPQL: "select m from Member m where m.name = ?"
     */
    @Override
    Optional<Member> findByName(String name);

    /**
     * JPQL: "select m from Member m where m.name = ? and m.id = ?"
     */
    Optional<Member> findByNameAndId(String name, Long id);
}
