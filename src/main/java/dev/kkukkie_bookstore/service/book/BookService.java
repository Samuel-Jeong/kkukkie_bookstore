package dev.kkukkie_bookstore.service.book;

import dev.kkukkie_bookstore.controller.item.book.form.BookAddForm;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.repository.item.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findById(String bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public void checkDuplicateBookAtAddByIsbn(BookAddForm bookAddForm,
                                              BindingResult bindingResult) {
        bookRepository.findByIsbn(bookAddForm.getIsbn())
                .ifPresent(
                        foundBook ->
                                bindingResult.reject(
                                        "BookAlreadyExist",
                                        new Object[]{foundBook.getIsbn()},
                                        "등록할 ISBN 이 이미 존재합니다."
                                )
                );
    }


}
