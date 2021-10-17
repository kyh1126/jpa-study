package jpabook.model.entity._3_복합키와식별관계매핑._2_복합키_비식별관계매핑_IdClass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

// 부모 클래스
@Entity
@IdClass(ParentId.class)
public class Parent1 {
    @Id
    @Column(name = "PARENT_ID1")
    private String id1; // ParentId.id1과 연결
    @Id
    @Column(name = "PARENT_ID2")
    private String id2; // ParentId.id2와 연결
    private String name;

    public String getId1() {
        return id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId(String id1, String id2) {
        this.id1 = id1;
        this.id2 = id2;
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
                "id1='" + id1 + '\'' +
                ", id2='" + id2 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
