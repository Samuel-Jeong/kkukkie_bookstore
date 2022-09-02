package dev.kkukkie_bookstore.controller.item.book;

import dev.kkukkie_bookstore.controller.item.book.form.BookAddForm;
import dev.kkukkie_bookstore.controller.item.book.form.BookDeleteForm;
import dev.kkukkie_bookstore.controller.item.book.form.BookUpdateForm;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.item.book.dto.BookDto;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.repository.item.BookRepository;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import dev.kkukkie_bookstore.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    private final MemberService memberService;
    private final MemberRepository memberRepository;


    public BookController(BookRepository bookRepository,
                          MemberService memberService, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("{memberId}/book/{bookId}")
    public String book(@PathVariable long memberId,
                       @PathVariable String bookId,
                       Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);

        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/book";
    }

    @GetMapping("/{memberId}")
    public String books(@PathVariable long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("memberId", memberId);

        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDtoList = new ArrayList<>();
        for (Book book : books) {
            bookDtoList.add(
                    new BookDto(
                            memberId,
                            book.getId(),
                            book.getName(),
                            book.getPrice(),
                            book.getCount(),
                            book.getIsbn(),
                            memberService.findBookByIdFromMember(member, book.getId()) != null
                    )
            );
        }
        model.addAttribute("bookDtoList", bookDtoList);

        if (member.getRole().equals(MemberRole.ADMIN)) {
            return "books/booksAdmin";
        } else {
            return "books/booksNormal";
        }
    }

    @GetMapping("/{memberId}/add")
    public String addForm(@PathVariable long memberId,
                          @ModelAttribute("book") Book book,
                          Model model) {
        model.addAttribute("memberId", memberId);
        return "books/addForm";
    }

    @PostMapping("/{memberId}/add")
    public String add(@PathVariable long memberId,
                      @Valid @ModelAttribute("book") BookAddForm bookAddForm,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        checkDuplicateBookAtAddByIsbn(bookAddForm, bindingResult);

        Book book = null;
        try {
            book = new Book(
                    UUID.randomUUID().toString(),
                    bookAddForm.getName(),
                    Integer.parseInt(bookAddForm.getPrice()),
                    Integer.parseInt(bookAddForm.getCount()),
                    bookAddForm.getIsbn()
            );
        } catch (Exception e) {
            bindingResult.reject("InputException", new Object[]{}, null);
        }

        if (book == null || bindingResult.hasErrors()) {
            return "books/addForm";
        }

        Book savedBook = bookRepository.save(book);
        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", savedBook.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/books/{memberId}/book/{bookId}";
    }

    private void checkDuplicateBookAtAddByIsbn(BookAddForm bookAddForm,
                                               BindingResult bindingResult) {
        bookRepository.findByIsbn(bookAddForm.getIsbn())
                .ifPresent(
                        foundBook ->
                                bindingResult.reject(
                                        "BookAlreadyExist",
                                        new Object[]{foundBook.getIsbn()},
                                        null
                                )
                );
    }

    @GetMapping("/{memberId}/book/{bookId}/edit")
    public String editForm(@PathVariable long memberId,
                           @PathVariable String bookId, Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/editForm";
    }

    @PostMapping("/{memberId}/book/{bookId}/edit")
    public String edit(@PathVariable long memberId,
                       @PathVariable String bookId,
                       @Validated @ModelAttribute("book") BookUpdateForm bookUpdateForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, null);
        }

        if (book != null) {
            try {
                book.setPrice(Integer.parseInt(bookUpdateForm.getPrice()));
                book.setCount(Integer.parseInt(bookUpdateForm.getCount()));
            } catch (Exception e) {
                bindingResult.reject("InputException", new Object[]{}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            return "books/editForm";
        }

        bookRepository.save(book);

        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", bookId);
        return "redirect:/books/{memberId}/book/{bookId}";
    }

    @GetMapping("/{memberId}/book/{bookId}/delete")
    public String deleteForm(@PathVariable long memberId,
                             @PathVariable String bookId,
                             Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/deleteForm";
    }

    @PostMapping("/{memberId}/book/{bookId}/delete")
    public String delete(@PathVariable long memberId,
                         @PathVariable String bookId,
                         @Validated @ModelAttribute("book") BookDeleteForm bookDeleteForm,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, null);
        }

        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", bookId);

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/books/{memberId}";
        }

        if (book != null) {
            bookRepository.delete(book);
        }

        return "redirect:/books/{memberId}";
    }

    @GetMapping("/{memberId}/addtolist/{bookId}")
    public String addBookModalForm(@PathVariable long memberId,
                                   @PathVariable String bookId,
                                   Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);
        return "books/addBookModal";
    }

    @PostMapping("/{memberId}/addtolist/{bookId}")
    public String addBookModal(@PathVariable long memberId,
                          @PathVariable String bookId,
                          @ModelAttribute("book") Book book,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, null);
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/books/{memberId}";
        }

        memberService.addBookToList(memberId, bookId);

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/books/{memberId}";
    }


    @GetMapping("/{memberId}/removefromlist/{bookId}")
    public String removeBookModalForm(@PathVariable long memberId,
                                   @PathVariable String bookId,
                                   Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);
        return "books/removeBookModal";
    }

    @PostMapping("/{memberId}/removefromlist/{bookId}")
    public String removeBookModal(@PathVariable long memberId,
                               @PathVariable String bookId,
                               @ModelAttribute("book") Book book,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, null);
        }

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/books/{memberId}";
        }

        memberService.removeBookFromList(memberId, bookId);

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/books/{memberId}";
    }

}
