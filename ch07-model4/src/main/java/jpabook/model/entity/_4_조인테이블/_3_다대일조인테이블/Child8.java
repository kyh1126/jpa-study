package jpabook.model.entity._4_조인테이블._3_다대일조인테이블;

import javax.persistence.*;
import java.util.Objects;

// 자식
@Entity
public class Child8 {
    @Id
    @GeneratedValue
    @Column(name = "CHILD_ID")
    private Long id;
    private String name;
    @ManyToOne(optional = false)
    @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
            joinColumns = @JoinColumn(name = "CHILD_ID"), // 현재 엔티티를 참조하는 외래 키
            inverseJoinColumns = @JoinColumn(name = "PARENT_ID") // 반대방향 엔티티를 참조하는 외래 키
    )
    private Parent8 parent;

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

    public Parent8 getParent() {
        return parent;
    }

    public void setParent(Parent8 parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Child8 child = (Child8) o;
        return Objects.equals(id, child.id) && Objects.equals(name, child.name) && Objects.equals(parent, child.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parent);
    }
}
