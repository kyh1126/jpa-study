package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_EmbeddedId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

// 부모 클래스
@Entity
public class Parent4 {
    @Id
    @Column(name = "PARENT_ID")
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
