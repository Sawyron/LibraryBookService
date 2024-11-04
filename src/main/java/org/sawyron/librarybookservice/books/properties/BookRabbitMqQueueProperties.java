package org.sawyron.librarybookservice.books.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "book-rabbitmq.queues")
public record BookRabbitMqQueueProperties(
        String exchange,
        String createQueue,
        String updateQueue,
        String deleteQueue
) {
}
