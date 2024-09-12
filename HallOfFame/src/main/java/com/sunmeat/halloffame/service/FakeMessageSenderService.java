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

@Service // исключительно для демонстрации задач, выполняемых по расписанию
public class FakeMessageSenderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String[] messages = {
    	    "Отличная работа!",
    	    "Очень полезный курс!",
    	    "Рекомендую всем!",
    	    "Интересный материал!",
    	    "Спасибо за информацию!",
    	    "Ничего не понятно, но очень интересно!",
    	    "Перестаньте разговаривать в нос с самим собой!",
    	    "0 из 10, никому не советую",
    	    "Предыдущий препод был лучше...",
    	    "Очень занятный урок, спасибо!",
    	    "Курс оказался сложнее, чем я думал",
    	    "Интерактивные задания помогли понять материал",
    	    "Подача материала отличная, но много повторений",
    	    "Прекрасный преподаватель, всё доходчиво объясняет",
    	    "Много полезной информации, но уроки длинные",
    	    "Не хватает практических примеров",
    	    "Сложный материал, но очень интересный!",
    	    "Спасибо за терпение, всё стало понятно",
    	    "Курс хороший, но много теории",
    	    "Все очень доступно, спасибо за качественный курс!",
    	    "Отличный преподаватель, всё объясняет ясно и понятно.",
    	    "Курс был сложным, но полезным",
    	    "Много примеров, всё очень наглядно",
    	    "Некоторые темы требовали более подробного объяснения",
    	    "Очень полезные задания, помогли усвоить материал",
    	    "Подача материала могла бы быть более динамичной",
    	    "Интересный формат уроков",
    	    "Спасибо за отличные примеры и объяснения",
    	    "Много полезных советов и рекомендаций",
    	    "Супер, но почему-то всё время хотелось выпить пива",
    	    "Курс требует много самостоятельной работы",
    	    "Все было понятно, но хотелось бы больше практики",
    	    "Хороший баланс теории и практики",
    	    "Отличное объяснение сложных тем",
    	    "Курс интересный, но требует много времени",
    	    "Благодарю за подробные объяснения и помощь 24/7"
    	};

    @Scheduled(fixedRate = 10000) // отправляем фейковый отзыв каждые 10 секунд
    public void sendFeedback() {
        var random = new Random();
        long profileId = random.nextInt(4) + 20; // случайные ID по имеющимся записям из БД

        // достаём детали из БД
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
            // если не попали в существующий ID
            feedbackMessage = new FeedbackMessage(profileId, "не помню его имя", "/uploads/teacher.jpg", "так себе", 5, "Одесса", messages[random.nextInt(messages.length)]);
        }

        // преобразование объекта в JSON
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(feedbackMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonString = "{}"; // fallback
        }

        // отправка JSON-сообщения в RabbitMQ как байтовый массив
        amqpTemplate.convertAndSend("feedbackExchange", "feedback.key", jsonString.getBytes(StandardCharsets.UTF_8));
    }
}