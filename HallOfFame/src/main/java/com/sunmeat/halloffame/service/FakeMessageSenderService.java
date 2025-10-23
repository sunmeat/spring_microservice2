package com.sunmeat.halloffame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.AmqpTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunmeat.halloffame.model.FeedbackMessage;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service // виключно для демонстрації - відправляє фейкові відгуки в RabbitMQ
public class FakeMessageSenderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String[] messages = {
    		"Відмінна робота!", 
    		"Дуже корисний курс!", 
    		"Рекомендую всім!", 
    		"Цікавий матеріал!", 
    		"Дякую за інформацію!", 
    		"Нічого не зрозуміло, але дуже цікаво!", 
    		"Перестаньте розмовляти в ніс із самим собою!", 
    		"0 із 10, нікому не раджу", 
    		"Попередній прєпод був кращим...", 
    		"Дуже цікавий урок, дякую!", 
    		"Курс виявився складнішим, ніж я думав", 
    		"Інтерактивні завдання допомогли зрозуміти матеріал!", 
    		"Подача матеріалу відмінна, але багато зайвих повторень", 
    		"Прекрасний викладач, все дохідливо пояснює", 
    		"Багато корисної інформації, але уроки довгі", 
    		"Не вистачає практичних прикладів", 
    		"Складний матеріал, але дуже цікавий!", 
    		"Дякую за терпіння, все стало зрозуміло", 
    		"Курс хороший, але занадто багато теорії", 
    		"Все дуже доступно, дякую за якісний курс!", 
    		"Чудовий викладач, все пояснює ясно і зрозуміло.", 
    		"Курс був складним, але корисним", 
    		"Багато прикладів, все дуже наочно", 
    		"Деякі теми вимагали більш докладного пояснення", 
    		"Дуже корисні завдання, допомогли засвоїти матеріал", 
    		"Подача матеріалу могла б бути більш динамічною", 
    		"Цікавий формат уроків", 
    		"Дякую за відмінні приклади та пояснення", 
    		"Багато корисних порад та рекомендацій", 
    		"Супер, але чомусь весь час хотілося випити пива...", 
    		"Курс вимагає багато самостійної роботи", 
    		"Все було зрозуміло, але хотілося б більше практики", 
    		"Хороший баланс теорії та практики", 
    		"Відмінне пояснення складних тем!", 
    		"Курс цікавий, але вимагає багато часу", 
    		"Дякую за докладні пояснення та допомогу 24/7"
    	};

    @SuppressWarnings({ "deprecation", "unused" })
	@Scheduled(fixedRate = 5000) // відправляє фейкові повідомлення кожні 5 секунд
    public void sendFeedback() {
        var random = new Random();
        long profileId = random.nextInt(5) + 5;

        // дістаємо дані профілю з бази даних
        var sql = "SELECT nickname, age, city, gender, avatar_url FROM profile WHERE id = ?";

        FeedbackMessage feedbackMessage;
        try {
            feedbackMessage = jdbcTemplate.queryForObject(sql, new Object[]{profileId}, (rs, rowNum) -> {
                var nickname = rs.getString("nickname");
                var age = rs.getInt("age");
                var city = rs.getString("city");
                var gender = rs.getString("gender");
                var avatarUrl = rs.getString("avatar_url");

                return new FeedbackMessage(profileId, nickname, avatarUrl, gender, age, city, messages[random.nextInt(messages.length)]);
            });
        } catch (Exception e) {
            // якщо профіль не знайдено, створюємо фейковий відгук
            feedbackMessage = new FeedbackMessage(profileId, "не пам'ятаю як звуть", "/uploads/teacher.jpg", "так собі", 5, "Одеса", messages[random.nextInt(messages.length)]);
        }

        // перетворення об'єкта FeedbackMessage у JSON
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(feedbackMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonString = "{}"; // fallback
        }

        // відправка JSON-повідомлення у RabbitMQ
        amqpTemplate.convertAndSend("feedbackExchange", "feedback.key", jsonString.getBytes(StandardCharsets.UTF_8));
    }
}
