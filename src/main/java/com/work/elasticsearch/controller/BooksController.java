package com.work.elasticsearch.controller;

import com.work.elasticsearch.entity.Books;
import com.work.elasticsearch.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Books> createBook(@RequestBody Books book) throws IOException {
        Books savedBook = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Books> getBookById(@PathVariable final String id) throws IOException {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Books> getAllBooks() throws IOException {
        return bookService.getAllBooks();
    }

    @GetMapping("/search")
    public List<Books> searchBooks(
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final String author
    ) throws IOException {
        if (title != null) {
            return bookService.searchBooksByTitle(title);
        } else if (author != null) {
            return bookService.searchBooksByAuthor(author);
        }
        return Collections.emptyList();
    }

}
