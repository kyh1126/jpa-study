package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_IdClass;

import java.io.Serializable;
import java.util.Objects;

// 자식 ID
public class ChildId implements Serializable {
    private String parent; // Child.parent 매핑
    private String childId; // Child.childId 매핑

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildId childId1 = (ChildId) o;
        return Objects.equals(parent, childId1.parent) && Objects.equals(childId, childId1.childId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, childId);
    }
}
