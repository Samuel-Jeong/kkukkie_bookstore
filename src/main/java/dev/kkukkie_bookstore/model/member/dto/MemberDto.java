package dev.kkukkie_bookstore.model.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import dev.kkukkie_bookstore.model.item.book.Book;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {

    private long id;
    private String username;
    private int age;

    private String loginId;

    private String role;

    private String teamName;
    private List<Book> books;

    @QueryProjection
    public MemberDto(long id, String username,
                     int age, String loginId,
                     String role, String teamName,
                     List<Book> books) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.loginId = loginId;
        this.role = role;
        this.teamName = teamName;

        this.books = new ArrayList<>(books);
        Collections.copy(this.books, books);
    }

}
