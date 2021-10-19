package jpabook.model.entity._4_조인테이블._3_다대일조인테이블;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 부모
@Entity
public class Parent8 {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "parent")
    private List<Child8> child = new ArrayList<>();

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

    public List<Child8> getChild() {
        return child;
    }

    public void setChild(List<Child8> child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent8 parent8 = (Parent8) o;
        return Objects.equals(id, parent8.id) && Objects.equals(name, parent8.name) && Objects.equals(child, parent8.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, child);
    }
}
