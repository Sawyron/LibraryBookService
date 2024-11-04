package org.sawyron.librarybookservice.books.impl;

import org.sawyron.librarybookservice.authors.Author;
import org.sawyron.librarybookservice.authors.AuthorRepository;
import org.sawyron.librarybookservice.books.Book;
import org.sawyron.librarybookservice.books.BookRepository;
import org.sawyron.librarybookservice.books.BookService;
import org.sawyron.librarybookservice.books.dtos.BookResponse;
import org.sawyron.librarybookservice.books.dtos.CreateBookMessage;
import org.sawyron.librarybookservice.books.dtos.UpdateBookMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class BookServiceImpl implements BookService {
    private final static Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final Function<Book, BookResponse> responseMapper;

    public BookServiceImpl(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            Function<Book, BookResponse> responseMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.responseMapper = responseMapper;
    }

    @Override
    public BookResponse findBookById(UUID id) {
        return bookRepository.findByIdWithAuthor(id)
                .map(responseMapper)
                .orElseThrow(() -> new RuntimeException("Book with id %s is not found".formatted(id)));
    }

    @Override
    @Transactional
    public void createBook(CreateBookMessage message) {
        Author author = authorRepository.findByNameIgnoreCase(message.author())
                .orElseGet(() -> {
                    var createdAuthor = new Author();
                    createdAuthor.setName(message.author());
                    authorRepository.save(createdAuthor);
                    logger.info("Author({}) saved", createdAuthor.getName());
                    return createdAuthor;
                });
        var book = new Book();
        boolean exists = bookRepository.existsByTitleAndAuthorIdAndPublishedDate(
                message.title(),
                author.getId(),
                message.publishedDate()
        );
        if (exists) {
            logger.info(
                    "Could not create book. A book ({}, {}, {}) already exits",
                    message.title(),
                    author.getName(),
                    message.publishedDate()
            );
            return;
        }
        book.setTitle(message.title());
        book.setPublishedDate(message.publishedDate());
        book.setAuthor(author);
        bookRepository.save(book);
        logger.info("Book ({}, {}) created", book.getTitle(), author.getName());
    }

    @Override
    @Transactional
    public void updateBook(UpdateBookMessage message) {
        Optional<Book> bookOptional = bookRepository.findByIdWithAuthor(message.id());
        if (bookOptional.isEmpty()) {
            logger.info("Book with id {} does not exists. Can not update", message.id());
            return;
        }
        Book book = bookOptional.get();
        book.setTitle(message.title());
        book.setPublishedDate(message.publishedDate());
        if (!book.getAuthor().getName().equals(message.author())) {
            var messageAuthor = authorRepository.findByNameIgnoreCase(message.author())
                    .orElseGet(() -> {
                        var createdAuthor = new Author();
                        createdAuthor.setName(message.author());
                        authorRepository.save(createdAuthor);
                        return createdAuthor;
                    });
            book.setAuthor(messageAuthor);
        }
        bookRepository.save(book);
        logger.info(
                "Book with id {} updated({}, {}, {})",
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getPublishedDate()
        );
    }

    @Override
    public void deleteBookById(UUID id) {
        bookRepository.deleteById(id);
        logger.info("Book with id {} deleted", id);
    }
}
