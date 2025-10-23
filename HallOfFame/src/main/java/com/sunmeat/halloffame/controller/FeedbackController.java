package com.sunmeat.halloffame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sunmeat.halloffame.service.FeedbackService;

@Controller
@RequestMapping("/reviews")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping  // отримує список відгуків і відображає їх на сторінці feedback-list
    public String listReviews(Model model) {
        model.addAttribute("feedbacks", feedbackService.getFeedbacks());
        return "feedback-list";
    }
}
