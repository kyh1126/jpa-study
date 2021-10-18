package jpabook.model.entity._3_복합키와식별관계매핑._4_비식별관계로구현;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Child5 {
    @Id
    @GeneratedValue
    @Column(name = "CHILD_ID")
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Parent5 parent;

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

    public Parent5 getParent() {
        return parent;
    }

    public void setParent(Parent5 parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Child5 child5 = (Child5) o;
        return Objects.equals(id, child5.id) && Objects.equals(name, child5.name) && Objects.equals(parent, child5.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parent);
    }
}
