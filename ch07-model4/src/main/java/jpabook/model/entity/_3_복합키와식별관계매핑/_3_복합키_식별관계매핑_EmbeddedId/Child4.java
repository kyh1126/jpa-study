package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_EmbeddedId;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Child4 {
    @EmbeddedId
    private ChildId childId;

    @MapsId("parentId") // ChildId.parentId 매핑
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public Parent4 parent;

    private String name;

    public ChildId getChildId() {
        return childId;
    }

    public void setChildId(ChildId childId) {
        this.childId = childId;
    }

    public Parent4 getParent() {
        return parent;
    }

    public void setParent(Parent4 parent) {
        this.parent = parent;
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
        Child4 child4 = (Child4) o;
        return Objects.equals(childId, child4.childId) && Objects.equals(parent, child4.parent) && Objects.equals(name, child4.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childId, parent, name);
    }
}
