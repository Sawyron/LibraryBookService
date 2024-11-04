package org.sawyron.librarybookservice.books;

import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        BookResponse book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll(@RequestParam int page, @RequestParam int pageSize) {
        List<BookResponse> books = bookService.findAllBooks(page, pageSize);
        return ResponseEntity.ok(books);
    }
}
