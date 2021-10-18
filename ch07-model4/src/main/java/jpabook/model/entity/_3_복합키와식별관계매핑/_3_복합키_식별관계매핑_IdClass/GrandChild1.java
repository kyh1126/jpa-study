package jpabook.model.entity._3_복합키와식별관계매핑._3_복합키_식별관계매핑_IdClass;

import javax.persistence.*;

// 손자
@Entity
@IdClass(GrandChildId.class)
public class GrandChild1 {
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    public Child3 child;
    @Id
    @Column(name = "GRANDCHILD_ID")
    private String id;
    private String name;
}
