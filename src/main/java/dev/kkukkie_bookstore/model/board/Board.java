package dev.kkukkie_bookstore.model.board;

import dev.kkukkie_bookstore.model.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;

    private String author;

    private String content;

    private int hitCount = 0;

    public Board(String title, String author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        setCreateDateTime(now);
        setLastModifiedDateTime(now);
    }

    @PreUpdate
    public void preUpdate() {
        setLastModifiedDateTime(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", title='" + title +
                '}';
    }

}
