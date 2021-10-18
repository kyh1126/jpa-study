package jpabook.model.entity._3_복합키와식별관계매핑._4_비식별관계로구현;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Parent5 {
    @Id @GeneratedValue
    @Column(name = "PARENT_ID")
    private Long id;
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent5 parent5 = (Parent5) o;
        return Objects.equals(id, parent5.id) && Objects.equals(name, parent5.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
