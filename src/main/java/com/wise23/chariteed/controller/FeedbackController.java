package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class FeedbackController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    // Mapped as /app/application
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Feedback send(final Feedback feedback) throws Exception {
        return feedback;
    }

    // Mapped as /app/private
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Feedback feedback) {
        simpMessagingTemplate.convertAndSendToUser(feedback.getTo(), "/specific", feedback);
    }
}
