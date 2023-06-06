package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// JPA 의 모든 데이터 변경이나 로직들은 트랜잭션 안에서 실행되는 것이 좋다.
// readOnly = true -> 데이터 변경이 없는 읽기 전용 메서드에 사용한다. 약간의 성능 향상.
@Service
@Transactional(readOnly = true)
//@AllArgsConstructor // 모든 필드를 가지고 생성자를 만들어준다.
@RequiredArgsConstructor // final 이 있는 필드만 가지고 생성자를 만들어준다. All 보다 더 나은 방법
public class MemberService {

    private final MemberRepository memberRepository;

    // 생성자 주입의 장점 -> 언제든지 다른 Repository를 주입할수 있다.
    // 생성자가 하나인 경우 Autowired 생략 가능
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional // readOnly = false(기본) 를 위해 따로 어노테이션 처리
    public Long join(Member member) {

        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) { // 비어있지 않고 존재한다면
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
