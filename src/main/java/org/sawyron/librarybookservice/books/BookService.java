package org.sawyron.librarybookservice.books;

import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.sawyron.librarybookservice.books.dtos.CreateBookMessage;
import org.sawyron.librarybookservice.books.dtos.UpdateBookMessage;

import java.util.UUID;

public interface BookService {
    BookResponse findBookById(UUID id);

    void createBook(CreateBookMessage message);

    void updateBook(UpdateBookMessage message);

    void deleteBookById(UUID id);
}
