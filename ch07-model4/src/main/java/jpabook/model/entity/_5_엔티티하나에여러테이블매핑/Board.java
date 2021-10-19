package jpabook.model.entity._5_엔티티하나에여러테이블매핑;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "BOARD")
@SecondaryTable(name = "BOARD_DETAIL",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAIL_ID"))
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;
    private String title; // table을 지정하지 않으면 기본 테이블인 Board(현재 테이블)에 매핑
    @Column(table = "BOARD_DETAIL") // table을 지정하면 해당 테이블에 매핑
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id) && Objects.equals(title, board.title) && Objects.equals(content, board.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content);
    }
}
