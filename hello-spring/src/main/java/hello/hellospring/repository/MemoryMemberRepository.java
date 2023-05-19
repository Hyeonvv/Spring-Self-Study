package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository{

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L; // 0,1,2 ... 키 값을 생성해주는 역할

    @Override
    public Member save(Member member) {
        member.setId(++sequence); // member id 값 세팅
        store.put(member.getId(), member); // 세팅된 id 값과, 해당 member 기입
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream() // 루프를 돌면서 member.getName() 이 parameter 로 받아온 name 과 같은 경우에만 필터링, 하나라도 찾으면 반환(findAny)
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
}
