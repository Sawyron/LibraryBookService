package org.sawyron.librarybookservice.books.mappers;

import org.sawyron.librarybookservice.books.Book;
import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BookResponseMapper implements Function<Book, BookResponse> {
    @Override
    public BookResponse apply(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getPublishedDate()
        );
    }
}
