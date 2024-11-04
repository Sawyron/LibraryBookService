package org.sawyron.librarybookservice.books;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
public class BooksController {
    @GetMapping("{id}")
    public ResponseEntity<?> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }
}
