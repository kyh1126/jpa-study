package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_EmbeddedId;

import javax.persistence.*;
import java.util.Objects;

// 손자
@Entity
public class GrandChild2 {
    @EmbeddedId
    private GrandChildId id;

    @MapsId("childId") // GrandChildId.childId 매핑
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    public Child4 child;

    private String name;

    public GrandChildId getId() {
        return id;
    }

    public void setId(GrandChildId id) {
        this.id = id;
    }

    public Child4 getChild() {
        return child;
    }

    public void setChild(Child4 child) {
        this.child = child;
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
        GrandChild2 that = (GrandChild2) o;
        return Objects.equals(id, that.id) && Objects.equals(child, that.child) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, child, name);
    }
}
