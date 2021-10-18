package jpabook.model.entity._3_복합키와식별관계매핑._5_일대일식별관계;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class BoardDetail {
    @Id
    private Long boardId;
    @MapsId // BoardDetail.boardId 매핑
    @OneToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;
    private String content;

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
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
        BoardDetail that = (BoardDetail) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(board, that.board) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, board, content);
    }
}
