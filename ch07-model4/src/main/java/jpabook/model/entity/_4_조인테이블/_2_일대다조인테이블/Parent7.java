package jpabook.model.entity._4_조인테이블._2_일대다조인테이블;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 부모
@Entity
public class Parent7 {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;
    @OneToMany
    @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
            joinColumns = @JoinColumn(name = "PARENT_ID"), // 현재 엔티티를 참조하는 외래 키
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID") // 반대방향 엔티티를 참조하는 외래 키
    )
    private List<Child7> child = new ArrayList<>();

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

    public List<Child7> getChild() {
        return child;
    }

    public void setChild(List<Child7> child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent7 parent7 = (Parent7) o;
        return Objects.equals(id, parent7.id) && Objects.equals(name, parent7.name) && Objects.equals(child, parent7.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, child);
    }
}
