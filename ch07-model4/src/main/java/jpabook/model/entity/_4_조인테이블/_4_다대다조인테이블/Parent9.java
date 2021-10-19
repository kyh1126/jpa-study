package jpabook.model.entity._4_조인테이블._4_다대다조인테이블;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Parent9 {
    @Id
    @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "parent")
    @JoinTable(name = "PARENT_CHILD",
            joinColumns = @JoinColumn(name = "PARENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID")
    )
    private List<Child9> child = new ArrayList<>();

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

    public List<Child9> getChild() {
        return child;
    }

    public void setChild(List<Child9> child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent9 parent = (Parent9) o;
        return Objects.equals(id, parent.id) && Objects.equals(name, parent.name) && Objects.equals(child, parent.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, child);
    }
}
