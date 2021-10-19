package jpabook.model.entity._4_조인테이블._4_다대다조인테이블;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Child9 {
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
        Child9 child = (Child9) o;
        return Objects.equals(id, child.id) && Objects.equals(name, child.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
