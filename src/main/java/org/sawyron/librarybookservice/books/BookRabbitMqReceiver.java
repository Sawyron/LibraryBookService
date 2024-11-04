package org.sawyron.librarybookservice.books;

import org.sawyron.librarybookservice.books.dtos.CreateBookMessage;
import org.sawyron.librarybookservice.books.dtos.UpdateBookMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookRabbitMqReceiver {
    private static final Logger logger = LoggerFactory.getLogger(BookRabbitMqReceiver.class);

    private final BookService bookService;

    public BookRabbitMqReceiver(BookService bookService) {
        this.bookService = bookService;
    }

    @RabbitListener(queues = "#{createBookQueue.name}")
    public void createHandler(@Payload CreateBookMessage message) {
        logger.info("create book message received: {}", message);
        bookService.createBook(message);
    }

    @RabbitListener(queues = "#{updateBookQueue.name}")
    public void updateHandler(@Payload UpdateBookMessage message) {
        logger.info("update book message received: {}", message);
        bookService.updateBook(message);
    }

    @RabbitListener(queues = "#{deleteBookQueue.name}")
    public void deleteHandler(@Payload UUID id) {
        logger.info("Book id for deleting received {}", id);
        bookService.deleteBookById(id);
    }
}
