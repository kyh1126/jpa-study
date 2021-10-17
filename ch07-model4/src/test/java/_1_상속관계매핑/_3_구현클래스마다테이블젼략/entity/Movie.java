package _1_상속관계매핑._3_구현클래스마다테이블젼략.entity;

import _1_상속관계매핑._2_단일테이블전략.entity.Item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // 엔티티 저장 시 구분 컬럼에 입력할 값을 지정.
public class Movie extends Item {
    private String director;
    private String actor;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
