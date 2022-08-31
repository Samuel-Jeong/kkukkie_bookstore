package dev.kkukkie_bookstore.model.item.book;

import dev.kkukkie_bookstore.model.item.base.Item;
import dev.kkukkie_bookstore.model.item.base.ItemType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Book extends Item {

    /**
     * ISBN (International Standard Book Number, 국제 표준 도서 번호)
     *
     *   13 자리의 숫자로 구성
     *
     *   ① 접두부(978 또는 979)
     *   ② 국별(또는 지역별 또는 언어별) 번호
     *   ③ 발행자(또는 발행처) 번호
     *   ④ 서명 식별 번호
     *   ⑤ 체크 기호
     *
     *   ex) ISBN 978-89-356-0120-2-03810
     */
    private String isbn;

    public Book(String id, String name, Integer price, Integer count, String isbn) {
        super(id, name, ItemType.BOOK, price, count);

        this.isbn = isbn;
    }

}
