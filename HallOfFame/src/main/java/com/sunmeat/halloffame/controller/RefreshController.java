package com.sunmeat.halloffame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sunmeat.halloffame.service.FeedbackService;
import com.sunmeat.halloffame.model.FeedbackMessage;
import java.util.List;

@RestController // використовується для створення RESTful веб-сервісів
@RequestMapping("/api/feedbacks")
public class RefreshController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public List<FeedbackMessage> getFeedbacks() {
        return feedbackService.getFeedbacks();
    }
}
