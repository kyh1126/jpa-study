package jpabook.model.entity._3_복합키와식별관계매핑._2_복합키_비식별관계매핑_EmbeddedId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Child2 {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1")
    @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
    // name 과 referencedColumnName 이 같으면 referencedColumnName 은 생략 가능하다.
    private Parent2 parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Parent2 getParent() {
        return parent;
    }

    public void setParent(Parent2 parent) {
        this.parent = parent;
    }
}
