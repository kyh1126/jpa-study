package jpabook.model.entity._3_복합키와식별관계매핑._4_비식별관계로구현;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class GrandChild3 {
    @Id
    @GeneratedValue
    @Column(name = "GRANDCHILD_ID")
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "CHILD_ID")
    private Child5 child;

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

    public Child5 getChild() {
        return child;
    }

    public void setChild(Child5 child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrandChild3 that = (GrandChild3) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, child);
    }
}
