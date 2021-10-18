package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_IdClass;

import javax.persistence.*;

@Entity
@IdClass(ChildId.class)
public class Child3 {
    @Id
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public Parent3 parent;

    @Id
    @Column(name = "CHILD_ID")
    private String childId;

    private String name;

    public Parent3 getParent() {
        return parent;
    }

    public void setParent(Parent3 parent) {
        this.parent = parent;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
