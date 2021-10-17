package _1_상속관계매핑._1_조인전략.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M") // 엔티티 저장 시 구분 컬럼에 입력할 값을 지정. 만약 영화 엔티티를 저장하면 구분 컬럼인 DTYPE 에 값 M이 저장된다.
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
