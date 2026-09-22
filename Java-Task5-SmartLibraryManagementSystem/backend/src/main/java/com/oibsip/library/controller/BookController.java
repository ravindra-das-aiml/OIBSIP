package com.oibsip.library.controller;

import com.oibsip.library.model.Book;
import com.oibsip.library.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryService libraryService;

    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Admin
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return libraryService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return libraryService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        libraryService.deleteBook(id);
    }

    // User + Admin
    @GetMapping
    public List<Book> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam String keyword) {
        return libraryService.searchBooks(keyword);
    }

    @GetMapping("/category/{category}")
    public List<Book> browseByCategory(@PathVariable String category) {
        return libraryService.browseByCategory(category);
    }
}
