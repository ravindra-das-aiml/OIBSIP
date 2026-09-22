package com.oibsip.library.service;

import com.oibsip.library.model.Book;
import com.oibsip.library.model.IssueRecord;
import com.oibsip.library.model.Member;
import com.oibsip.library.repository.BookRepository;
import com.oibsip.library.repository.IssueRecordRepository;
import com.oibsip.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final IssueRecordRepository issueRecordRepository;

    public LibraryService(BookRepository bookRepository,
                           MemberRepository memberRepository,
                           IssueRecordRepository issueRecordRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.issueRecordRepository = issueRecordRepository;
    }

    // ---------- Admin: catalogue management ----------

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book updated) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setCategory(updated.getCategory());
        existing.setTotalQuantity(updated.getTotalQuantity());
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    public List<Book> browseByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }

    // ---------- User: issue / return ----------

    public IssueRecord issueBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("No copies available — consider reserving instead");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        IssueRecord record = new IssueRecord(member, book);
        return issueRecordRepository.save(record);
    }

    public IssueRecord returnBook(Long issueRecordId) {
        IssueRecord record = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found"));

        record.markReturned();

        Book book = record.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        if (record.getFineAmount() > 0) {
            Member member = record.getMember();
            member.setOutstandingFine(member.getOutstandingFine() + record.getFineAmount());
            memberRepository.save(member);
        }

        return issueRecordRepository.save(record);
    }

    public IssueRecord reserveBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        IssueRecord record = new IssueRecord(member, book);
        record.setStatus(IssueRecord.Status.RESERVED);
        return issueRecordRepository.save(record);
    }

    // ---------- Fines ----------

    public void payFine(Long issueRecordId) {
        IssueRecord record = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Issue record not found"));
        record.setFinePaid(true);
        issueRecordRepository.save(record);

        Member member = record.getMember();
        member.setOutstandingFine(Math.max(0, member.getOutstandingFine() - record.getFineAmount()));
        memberRepository.save(member);
    }

    public List<IssueRecord> getMemberHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return issueRecordRepository.findByMember(member);
    }

    public List<IssueRecord> getAllIssuedBooks() {
        return issueRecordRepository.findByStatus(IssueRecord.Status.ISSUED);
    }
}
