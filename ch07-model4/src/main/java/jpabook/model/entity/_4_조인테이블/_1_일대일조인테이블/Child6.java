package jpabook.model.entity._4_조인테이블._1_일대일조인테이블;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

// 자식
@Entity
public class Child6 {
    @Id
    @GeneratedValue
    @Column(name = "CHILD_ID")
    private Long id;
    private String name;

    // 양방향 매핑 시
    // @OneToOne(mappedBy="child")
    // private Parent parent;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Child6 child6 = (Child6) o;
        return Objects.equals(id, child6.id) && Objects.equals(name, child6.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
