package _1_상속관계매핑._2_단일테이블전략.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 테이블 하나에 모든 것을 통합, 구분 컬럼 필수 => 단일 테이블 전략
@DiscriminatorColumn(name = "DTYPE") // 부모 클래스에 구분 컬럼을 지정. 이 컬럼으로 저장된 자식 테이블을 구분할 수 있다.
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void change(Long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
