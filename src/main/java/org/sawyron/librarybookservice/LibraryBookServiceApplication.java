package org.sawyron.librarybookservice;

import org.sawyron.librarybookservice.books.properties.BookRabbitMqQueueProperties;
import org.sawyron.librarybookservice.books.properties.BookRabbitMqRouteProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BookRabbitMqQueueProperties.class, BookRabbitMqRouteProperties.class})
public class LibraryBookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryBookServiceApplication.class, args);
    }

}
