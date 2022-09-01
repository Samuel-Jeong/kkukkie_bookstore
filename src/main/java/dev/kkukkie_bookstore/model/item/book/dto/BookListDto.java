package dev.kkukkie_bookstore.model.item.book.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookListDto {

    private Map<String, BookDto> bookDtoMap;

    public BookListDto(Map<String, BookDto> bookDtoMap) {
        this.bookDtoMap = bookDtoMap;
    }

    public BookDto getBookDto(String key) {
        return bookDtoMap.get(key);
    }

}
