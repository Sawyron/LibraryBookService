package org.sawyron.librarybookservice.books.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("book-rabbitmq.routes")
public record BookRabbitMqRouteProperties(
        String createRoute,
        String updateRoute,
        String deleteRoute
) {
}
