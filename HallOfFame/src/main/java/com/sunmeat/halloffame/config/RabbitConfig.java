package com.sunmeat.halloffame.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // используется для обозначения класса как источника конфигурации Spring, который определяет бины (объекты) и настройки приложения
@EnableRabbit // Spring Boot инициализирует компоненты, необходимые для работы с RabbitMQ
public class RabbitConfig {

    @Bean // создаёт очередь с именем "feedbackQueue"
    Queue feedbackQueue() {
        return new Queue("feedbackQueue", false);
    }

    @Bean // создаёт обменник с именем "feedbackExchange"
    // обменник (или exchange) — это компонент, который принимает сообщения от отправителей и маршрутизирует их в одну или несколько очередей сообщений в зависимости от правил маршрутизации
    TopicExchange feedbackExchange() {
        return new TopicExchange("feedbackExchange");
    }

    @Bean // создаёт привязку между очередью и обменником с использованием ключа маршрутизации
    Binding binding(Queue feedbackQueue, TopicExchange feedbackExchange) {
        return BindingBuilder.bind(feedbackQueue).to(feedbackExchange).with("feedback.#");
    }

    @Bean // создаёт конвертер сообщений для преобразования объектов в JSON и обратно
    MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean // создаёт фабрику соединений с RabbitMQ
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean // создаёт RabbitTemplate для отправки и получения сообщений
    // это компонент в Spring AMQP, который предоставляет высокоуровневый API для взаимодействия с RabbitMQ, существенно облегчает отправку и получение сообщений в RabbitMQ
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2MessageConverter());
        return template;
    }
}