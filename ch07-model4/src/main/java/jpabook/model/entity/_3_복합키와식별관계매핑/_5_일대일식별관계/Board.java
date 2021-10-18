package jpabook.model.entity._3_복합키와식별관계매핑._5_일대일식별관계;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Board {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;
    private String title;
    @OneToOne(mappedBy = "board")
    private BoardDetail boardDetail;

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

    public BoardDetail getBoardDetail() {
        return boardDetail;
    }

    public void setBoardDetail(BoardDetail boardDetail) {
        this.boardDetail = boardDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id) && Objects.equals(title, board.title) && Objects.equals(boardDetail, board.boardDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, boardDetail);
    }
}
