package dev.kkukkie_bookstore.service.member;

import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.repository.item.BookRepository;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MemberService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public MemberService(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void addBookToList(long memberId, String bookId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) { return; }

            Book foundBook = findBookByIdFromMember(member, bookId);
            if (foundBook == null) {
                member.getBooks().add(book);
                memberRepository.save(member);
            }
        }
    }

    @Transactional
    public void removeBookFromList(long memberId, String bookId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) { return; }

            member.getBooks().remove(book);
            memberRepository.save(member);
        }
    }

    public Book findBookByIdFromMember(Member member, String bookId) {
        return member.getBooks().stream().filter(
                item1 -> item1.getId().equals(bookId)
        ).findAny().orElse(null);
    }

}
