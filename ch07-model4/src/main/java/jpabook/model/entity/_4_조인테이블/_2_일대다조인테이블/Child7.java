package jpabook.model.entity._4_조인테이블._2_일대다조인테이블;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

// 자식
@Entity
public class Child7 {
    @Id
    @GeneratedValue
    @Column(name = "CHILD_ID")
    private Long id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Child7 child7 = (Child7) o;
        return Objects.equals(id, child7.id) && Objects.equals(name, child7.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
