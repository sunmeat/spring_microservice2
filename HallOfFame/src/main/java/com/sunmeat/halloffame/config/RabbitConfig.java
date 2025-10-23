package com.sunmeat.halloffame.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // використовується для позначення класу як джерела бінів конфігурації Spring
@EnableRabbit // Spring Boot ініціалізує підтримку RabbitMQ
public class RabbitConfig {

    @Bean // створює чергу з назвою "feedbackQueue"
    Queue feedbackQueue() {
        return new Queue("feedbackQueue", false);
    }

    @Bean // створює обмінник (exchange) типу Topic з назвою "feedbackExchange"
    // обмінник - це компонент RabbitMQ, який отримує повідомлення від виробників і маршрутизує їх до відповідних черг на основі визначених правил маршрутизації
    TopicExchange feedbackExchange() {
        return new TopicExchange("feedbackExchange");
    }

    @Bean // створює прив’язку черги до обмінника з шаблоном маршрутизації "feedback.#"
    Binding binding(Queue feedbackQueue, TopicExchange feedbackExchange) {
        return BindingBuilder.bind(feedbackQueue).to(feedbackExchange).with("feedback.#");
    }

    @Bean // створює конвертер повідомлень для перетворення об'єктів Java в JSON і навпаки
    MessageConverter jackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean // cтворює фабрику з'єднань з RabbitMQ, вказуючи локальний сервер
    ConnectionFactory connectionFactory() {
        var connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean // створює RabbitTemplate, який використовується для відправки та отримання повідомлень з RabbitMQ
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2MessageConverter());
        return template;
    }
}
