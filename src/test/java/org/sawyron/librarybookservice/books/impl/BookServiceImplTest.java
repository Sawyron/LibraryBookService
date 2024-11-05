package org.sawyron.librarybookservice.books.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sawyron.librarybookservice.authors.Author;
import org.sawyron.librarybookservice.authors.AuthorRepository;
import org.sawyron.librarybookservice.books.Book;
import org.sawyron.librarybookservice.books.BookRepository;
import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.sawyron.librarybookservice.books.dtos.CreateBookMessage;
import org.sawyron.librarybookservice.books.dtos.UpdateBookMessage;
import org.sawyron.librarybookservice.books.mappers.BookResponseMapper;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    AuthorRepository authorRepository;

    @Spy
    Function<Book, BookResponse> responseMapper = new BookResponseMapper();

    @Test
    void whenTheSameBookIsNotExistsAndAuthorIsPresent_thenCreateBook() {
        var createBookMessage = new CreateBookMessage(
                "title",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        var author = new Author();
        author.setId(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));
        author.setName("author");
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        when(authorRepository.findByNameIgnoreCase(eq(author.getName()))).thenReturn(Optional.of(author));
        when(bookRepository.existsByTitleAndAuthorIdAndPublishedDate(
                eq(createBookMessage.title()),
                eq(author.getId()),
                eq(createBookMessage.publishedDate())
        )).thenReturn(false);

        bookService.createBook(createBookMessage);

        verify(authorRepository).findByNameIgnoreCase(author.getName());
        verify(bookRepository).existsByTitleAndAuthorIdAndPublishedDate(
                createBookMessage.title(),
                author.getId(),
                createBookMessage.publishedDate()
        );
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book actualBook = bookArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(createBookMessage.title(), actualBook.getTitle()),
                () -> assertEquals(createBookMessage.author(), actualBook.getAuthor().getName()),
                () -> assertEquals(createBookMessage.publishedDate(), actualBook.getPublishedDate())
        );
    }

    @Test
    void whenTheSameBookIsNotExistsAndAuthorIssNotPresent_thenCreateBookAndAuthor() {
        var createBookMessage = new CreateBookMessage(
                "title",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        String authorName = "author";
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        when(authorRepository.findByNameIgnoreCase(eq(authorName))).thenReturn(Optional.empty());
        when(bookRepository.existsByTitleAndAuthorIdAndPublishedDate(
                eq(createBookMessage.title()),
                eq(null),
                eq(createBookMessage.publishedDate())
        )).thenReturn(false);

        bookService.createBook(createBookMessage);

        verify(authorRepository).findByNameIgnoreCase(authorName);
        verify(bookRepository).existsByTitleAndAuthorIdAndPublishedDate(
                createBookMessage.title(),
                null,
                createBookMessage.publishedDate()
        );
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book actualBook = bookArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(createBookMessage.title(), actualBook.getTitle()),
                () -> assertEquals(createBookMessage.author(), actualBook.getAuthor().getName()),
                () -> assertEquals(createBookMessage.publishedDate(), actualBook.getPublishedDate())
        );
        verify(authorRepository).save(authorCaptor.capture());
        Author actualAuthor = authorCaptor.getValue();
        assertAll(
                () -> assertNull(actualAuthor.getId()),
                () -> assertEquals(authorName, actualAuthor.getName())
        );
    }

    @Test
    void whenTheSameBookExists_thenDoesNotSave() {
        var createBookMessage = new CreateBookMessage(
                "title",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        var author = new Author();
        author.setId(UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"));
        author.setName("author");
        when(authorRepository.findByNameIgnoreCase(eq(author.getName()))).thenReturn(Optional.of(author));
        when(bookRepository.existsByTitleAndAuthorIdAndPublishedDate(
                eq(createBookMessage.title()),
                eq(author.getId()),
                eq(createBookMessage.publishedDate())
        )).thenReturn(true);

        bookService.createBook(createBookMessage);

        verify(authorRepository).findByNameIgnoreCase(author.getName());
        verify(bookRepository).existsByTitleAndAuthorIdAndPublishedDate(
                createBookMessage.title(),
                author.getId(),
                createBookMessage.publishedDate()
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void whenBookExists_thenUpdateItWithoutAuthorChange() {
        var updateMessage = new UpdateBookMessage(
                UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"),
                "title2",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        var author = new Author();
        author.setId(UUID.fromString("d445f4f5-0eda-4f7c-a36c-7ecc4ad4168a"));
        author.setName("AUTHOR");
        var book = new Book();
        book.setAuthor(author);
        book.setId(updateMessage.id());
        book.setTitle("title");
        book.setPublishedDate(LocalDate.of(2019, 5, 12));
        when(bookRepository.findByIdWithAuthor(eq(updateMessage.id()))).thenReturn(Optional.of(book));

        bookService.updateBook(updateMessage);

        verify(bookRepository).findByIdWithAuthor(updateMessage.id());
        verify(bookRepository).save(book);
        verifyNoMoreInteractions(authorRepository);
        assertAll(
                () -> assertEquals(updateMessage.title(), book.getTitle()),
                () -> assertTrue(updateMessage.author().equalsIgnoreCase(book.getAuthor().getName()))
        );
    }

    @Test
    void whenBookExists_thenUpdateItWithAuthorChange() {
        var updateMessage = new UpdateBookMessage(
                UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"),
                "title2",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        var oldAuthor = new Author();
        oldAuthor.setId(UUID.fromString("d445f4f5-0eda-4f7c-a36c-7ecc4ad4168a"));
        oldAuthor.setName("AUTHOR2");
        var book = new Book();
        book.setAuthor(oldAuthor);
        book.setId(updateMessage.id());
        book.setTitle("title");
        book.setPublishedDate(LocalDate.of(2019, 5, 12));
        when(bookRepository.findByIdWithAuthor(eq(updateMessage.id()))).thenReturn(Optional.of(book));
        var newAuthor = new Author();
        newAuthor.setName("AUTHOR");
        when(authorRepository.findByNameIgnoreCase(eq("author")))
                .thenReturn(Optional.of(newAuthor));

        bookService.updateBook(updateMessage);

        verify(bookRepository).findByIdWithAuthor(updateMessage.id());
        verify(bookRepository).save(book);
        verify(authorRepository).findByNameIgnoreCase("author");
        verifyNoMoreInteractions(authorRepository);
        assertAll(
                () -> assertEquals(updateMessage.title(), book.getTitle()),
                () -> assertTrue(updateMessage.author().equalsIgnoreCase(book.getAuthor().getName()))
        );
    }

    @Test
    void whenBookExists_thenUpdateItWithAuthorCreate() {
        var updateMessage = new UpdateBookMessage(
                UUID.fromString("bbcc4621-d88f-4a94-ae2f-b38072bf5087"),
                "title2",
                "author",
                LocalDate.of(2020, 7, 12)
        );
        var oldAuthor = new Author();
        oldAuthor.setId(UUID.fromString("d445f4f5-0eda-4f7c-a36c-7ecc4ad4168a"));
        oldAuthor.setName("AUTHOR2");
        var book = new Book();
        book.setAuthor(oldAuthor);
        book.setId(updateMessage.id());
        book.setTitle("title");
        book.setPublishedDate(LocalDate.of(2019, 5, 12));
        when(bookRepository.findByIdWithAuthor(eq(updateMessage.id()))).thenReturn(Optional.of(book));
        when(authorRepository.findByNameIgnoreCase(eq("author")))
                .thenReturn(Optional.empty());

        bookService.updateBook(updateMessage);

        verify(bookRepository).findByIdWithAuthor(updateMessage.id());
        verify(bookRepository).save(book);
        verify(authorRepository).findByNameIgnoreCase("author");
        verify(authorRepository).save(any());
        verifyNoMoreInteractions(authorRepository);
        assertAll(
                () -> assertEquals(updateMessage.title(), book.getTitle()),
                () -> assertTrue(updateMessage.author().equalsIgnoreCase(book.getAuthor().getName()))
        );
    }
}