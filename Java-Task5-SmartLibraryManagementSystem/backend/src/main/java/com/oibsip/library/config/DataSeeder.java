package com.oibsip.library.config;

import com.oibsip.library.model.Book;
import com.oibsip.library.model.Member;
import com.oibsip.library.repository.BookRepository;
import com.oibsip.library.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seeds sample data on startup so the app + demo video work with zero manual setup. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public DataSeeder(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        bookRepository.save(new Book("Clean Code", "Robert C. Martin", "9780132350884", "Programming", 3));
        bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", "Programming", 2));
        bookRepository.save(new Book("Head First Design Patterns", "Eric Freeman", "9780596007126", "Programming", 2));
        bookRepository.save(new Book("Deep Learning", "Ian Goodfellow", "9780262035613", "AI/ML", 2));
        bookRepository.save(new Book("Hands-On Machine Learning", "Aurelien Geron", "9781492032649", "AI/ML", 3));
        bookRepository.save(new Book("Pattern Recognition and ML", "Christopher Bishop", "9780387310732", "AI/ML", 1));
        bookRepository.save(new Book("Sapiens", "Yuval Noah Harari", "9780062316097", "History", 4));
        bookRepository.save(new Book("Guns, Germs, and Steel", "Jared Diamond", "9780393317558", "History", 2));

        Member admin = new Member("Admin", "admin@library.com", "admin123");
        admin.setAdmin(true);
        memberRepository.save(admin);

        memberRepository.save(new Member("Ravindra Das", "ravindra@example.com", "pass123"));

        System.out.println("✅ Sample data loaded: 8 books, 2 members (admin@library.com / ravindra@example.com)");
    }
}
