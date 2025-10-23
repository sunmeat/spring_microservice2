package com.sunmeat.halloffame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunmeat.halloffame.model.FeedbackMessage;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    private List<FeedbackMessage> feedbacks = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public FeedbackService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper; // використовується для серіалізації та десеріалізації JSON
    }

    @RabbitListener(queues = "feedbackQueue")
    public void receiveMessage(byte[] message) {
        try {
            var jsonMessage = new String(message, StandardCharsets.UTF_8);
            FeedbackMessage feedbackMessage = objectMapper.readValue(jsonMessage, FeedbackMessage.class);
            feedbackMessage.setAvatarUrl("http://localhost:8080" + feedbackMessage.getAvatarUrl());
            System.out.println("Повідомлення: " + feedbackMessage);
            feedbacks.add(feedbackMessage);
        } catch (Exception e) {
            System.out.println("Помилка при відправці: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<FeedbackMessage> getFeedbacks() {
        return feedbacks;
    }
}
