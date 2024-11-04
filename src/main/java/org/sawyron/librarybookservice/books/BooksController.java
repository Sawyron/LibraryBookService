package org.sawyron.librarybookservice.books;

import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBookById(@PathVariable UUID id) {
        BookResponse book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }
}
