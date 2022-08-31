package dev.kkukkie_bookstore.model.item.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookDto {

    private long memberId;
    private String id;
    private String name;
    private int price;
    private int count;
    private String isbn;

    @QueryProjection
    public BookDto(long memberId, String id, String name, int price, int count, String isbn) {
        this.memberId = memberId;
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
        this.isbn = isbn;
    }
}
