package dev.kkukkie_bookstore.repository.item;

import dev.kkukkie_bookstore.model.item.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String> {

    Optional<Book> findByName(String name);

    Optional<Book> findByIsbn(String isbn);

}
