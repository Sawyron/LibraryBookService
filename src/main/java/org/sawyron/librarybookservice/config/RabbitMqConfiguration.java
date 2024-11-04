package org.sawyron.librarybookservice.config;

import org.sawyron.librarybookservice.books.properties.BookRabbitMqQueueProperties;
import org.sawyron.librarybookservice.books.properties.BookRabbitMqRouteProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {
    private final BookRabbitMqQueueProperties queueProperties;
    private final BookRabbitMqRouteProperties routeProperties;

    public RabbitMqConfiguration(BookRabbitMqQueueProperties queueProperties, BookRabbitMqRouteProperties routeProperties) {
        this.queueProperties = queueProperties;
        this.routeProperties = routeProperties;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(queueProperties.exchange());
    }

    @Bean
    public Queue createBookQueue() {
        return new Queue(queueProperties.createQueue(), false);
    }

    @Bean
    public Queue updateBookQueue() {
        return new Queue(queueProperties.updateQueue(), false);
    }

    @Bean
    public Queue deleteBookQueue() {
        return new Queue(queueProperties.deleteQueue(), false);
    }

    @Bean
    public Binding createBinding() {
        return BindingBuilder.bind(createBookQueue())
                .to(directExchange())
                .with(routeProperties.createRoute());
    }

    @Bean
    public Binding updateBinding() {
        return BindingBuilder.bind(updateBookQueue())
                .to(directExchange())
                .with(routeProperties.updateRoute());
    }

    @Bean
    public Binding deleteBinding() {
        return BindingBuilder.bind(deleteBookQueue())
                .to(directExchange())
                .with(routeProperties.deleteRoute());
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
