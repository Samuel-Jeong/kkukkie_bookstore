package dev.jmagni.repository.item;

import dev.jmagni.model.item.book.Book;
import org.aspectj.weaver.loadtime.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findById(String id);

    Optional<Book> findByName(String name);

    Optional<Book> findByIsbn(String isbn);

}
