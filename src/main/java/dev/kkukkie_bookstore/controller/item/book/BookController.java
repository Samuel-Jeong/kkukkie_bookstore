package dev.kkukkie_bookstore.controller.item.book;

import dev.kkukkie_bookstore.controller.item.book.form.BookAddForm;
import dev.kkukkie_bookstore.controller.item.book.form.BookDeleteForm;
import dev.kkukkie_bookstore.controller.item.book.form.BookUpdateForm;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.item.book.dto.BookDto;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.service.book.BookService;
import dev.kkukkie_bookstore.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;

    public BookController(BookService bookService,
                          MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @GetMapping("{memberId}/book/{bookId}")
    public String book(@PathVariable long memberId,
                       @PathVariable String bookId,
                       Model model) {
        Book book = bookService.findById(bookId);

        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/book";
    }

    @GetMapping("/{memberId}")
    public String books(@PathVariable long memberId, Model model) {
        Member member = memberService.findById(memberId);
        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("memberId", memberId);

        List<Book> books = bookService.findAll();
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
        bookService.checkDuplicateBookAtAddByIsbn(bookAddForm, bindingResult);

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
            bindingResult.reject("InputException", new Object[]{}, "입력값 확인이 필요합니다.");
        }

        if (book == null || bindingResult.hasErrors()) {
            return "books/addForm";
        }

        Book savedBook = bookService.save(book);
        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", savedBook.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/books/{memberId}/book/{bookId}";
    }

    @GetMapping("/{memberId}/book/{bookId}/edit")
    public String editForm(@PathVariable long memberId,
                           @PathVariable String bookId, Model model) {
        Book book = bookService.findById(bookId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/editForm";
    }

    @PostMapping("/{memberId}/book/{bookId}/edit")
    public String edit(@PathVariable long memberId,
                       @PathVariable String bookId,
                       @Validated @ModelAttribute("book") BookUpdateForm bookUpdateForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, "책이 존재하지 않습니다.");
        }

        if (book != null) {
            try {
                book.setPrice(Integer.parseInt(bookUpdateForm.getPrice()));
                book.setCount(Integer.parseInt(bookUpdateForm.getCount()));
            } catch (Exception e) {
                bindingResult.reject("InputException", new Object[]{}, "입력값 확인이 필요합니다.");
            }
        }

        if (bindingResult.hasErrors()) {
            return "books/editForm";
        }

        bookService.save(book);

        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", bookId);
        return "redirect:/books/{memberId}/book/{bookId}";
    }

    @GetMapping("/{memberId}/book/{bookId}/delete")
    public String deleteForm(@PathVariable long memberId,
                             @PathVariable String bookId,
                             Model model) {
        Book book = bookService.findById(bookId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);

        return "books/deleteForm";
    }

    @PostMapping("/{memberId}/book/{bookId}/delete")
    public String delete(@PathVariable long memberId,
                         @PathVariable String bookId,
                         @Validated @ModelAttribute("book") BookDeleteForm bookDeleteForm,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        Book book = bookService.findById(bookId);
        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, "책이 존재하지 않습니다.");
        }

        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("bookId", bookId);

        if (bindingResult.hasErrors()) {
            //log.warn("errors={}", bindingResult);
            return "redirect:/books/{memberId}";
        }

        if (book != null) {
            bookService.delete(book);
        }

        return "redirect:/books/{memberId}";
    }

    @GetMapping("/{memberId}/addtolist/{bookId}")
    public String addBookModalForm(@PathVariable long memberId,
                                   @PathVariable String bookId,
                                   Model model) {
        Book book = bookService.findById(bookId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("book", book);
        return "books/addBookModal";
    }

    @PostMapping("/{memberId}/addtolist/{bookId}")
    public String addBookModal(@PathVariable long memberId,
                          @PathVariable String bookId,
                          @ModelAttribute("book") Book book,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("memberId", memberId);

        if (book == null) {
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, "책이 존재하지 않습니다.");
            return "redirect:/books/{memberId}";
        }

        Member member = memberService.findById(memberId);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        if (!memberService.addBookToList(memberId, bookId)) {
            bindingResult.reject("FailToAddBookToList", new Object[]{memberId, bookId}, "추가하지 못했습니다. 존재하지 않는 책이거나 수량이 부족합니다.");

            // 화면에 에러가 출력이 안되서 일단 주석처리하고 책 목록으로 돌아가기 변경
            /*Book foundBook = bookRepository.findById(bookId).orElse(null);
            redirectAttributes.addAttribute("book", foundBook);
            return "redirect:/books/{memberId}/addtolist/{bookId}";*/

            return "redirect:/books/{memberId}";
        }

        return "redirect:/books/{memberId}";
    }

    @GetMapping("/{memberId}/removefromlist/{bookId}")
    public String removeBookModalForm(@PathVariable long memberId,
                                   @PathVariable String bookId,
                                   Model model) {
        Book book = bookService.findById(bookId);
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
            bindingResult.reject("NotFoundBook", new Object[]{bookId}, "책이 존재하지 않습니다.");
            return "redirect:/books/{memberId}";
        }

        Member member = memberService.findById(memberId);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        memberService.removeBookFromList(memberId, bookId);

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/books/{memberId}";
    }

}
