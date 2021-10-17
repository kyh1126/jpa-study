package _1_상속관계매핑._2_단일테이블전략.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
public class Album extends Item {
    private String author;

    public Album(Long id, String name, int price, String author) {
        super.change(id, name, price); // 부모 클래스의 정보를 넣어줌.
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
