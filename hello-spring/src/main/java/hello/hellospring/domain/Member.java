package hello.hellospring.domain;

import javax.persistence.*;

@Entity // JPA 가 관리하는 Entity
public class Member {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id 값 자동 생성
    private Long id;

//    @Column(name = "username") // 컬럼 명 설정 가능
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
