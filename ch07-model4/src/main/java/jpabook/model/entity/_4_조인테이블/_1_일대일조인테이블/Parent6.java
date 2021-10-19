package jpabook.model.entity._4_조인테이블._1_일대일조인테이블;

import javax.persistence.*;
import java.util.Objects;

// 부모
@Entity
public class Parent6 {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;
    @OneToOne
    @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
            joinColumns = @JoinColumn(name = "PARENT_ID"), // 현재 엔티티를 참조하는 외래 키
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID") // 반대방향 엔티티를 참조하는 외래 키
    )
    private Child6 child;

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

    public Child6 getChild() {
        return child;
    }

    public void setChild(Child6 child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent6 parent6 = (Parent6) o;
        return Objects.equals(id, parent6.id) && Objects.equals(name, parent6.name) && Objects.equals(child, parent6.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, child);
    }
}
