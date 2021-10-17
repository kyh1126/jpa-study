package jpabook.model.entity._3_복합키와식별관계매핑._2_복합키_비식별관계매핑_EmbeddedId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

// 부모 클래스
@Entity
public class Parent2 {
    @EmbeddedId
    private ParentId id;
    private String name;

    public ParentId getId() {
        return id;
    }

    public void setId(ParentId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
