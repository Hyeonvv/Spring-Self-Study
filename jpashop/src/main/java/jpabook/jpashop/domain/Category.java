package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    /**
     * 실무에서는 ManytoMany 를 사용하지 말자, 중간 테이블에 컬럼을 추가할 수도 없고, 세밀하게 쿼리를 실행하기 어렵다.
     * 중간 엔티티를 만들고, @ManyToOne, @OneToMany 로 매핑해서 사용하는것이 좋다. 다대다 매핑을 일대다, 다대일 매핑으로 풀어내자.
     */
    @ManyToMany
    @JoinTable(name = "category_item", // 중간 테이블
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    /**
     * 같은 엔티티 내에서 양방향 관계를 걸었다.
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
