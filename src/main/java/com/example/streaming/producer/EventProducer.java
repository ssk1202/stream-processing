package com.example.streaming.producer;

import com.example.streaming.model.UserActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class EventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);
    private static final String TOPIC_NAME = "user-activities";
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    
    private final String[] userIds = {"user1", "user2", "user3", "user4", "user5"};
    private final String[] actions = {"login", "logout", "purchase", "view_product", "add_to_cart", "search"};

    @Autowired
    public EventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 2000) // Produce events every 2 seconds
    public void produceUserActivity() {
        try {
            UserActivity activity = generateRandomActivity();
            String message = objectMapper.writeValueAsString(activity);
            
            kafkaTemplate.send(TOPIC_NAME, activity.getUserId(), message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Produced event: userId={}, action={}, value={}", 
                                    activity.getUserId(), activity.getAction(), activity.getValue());
                        } else {
                            logger.error("Failed to produce event", ex);
                        }
                    });
        } catch (Exception e) {
            logger.error("Error producing user activity", e);
        }
    }

    private UserActivity generateRandomActivity() {
        String userId = userIds[random.nextInt(userIds.length)];
        String action = actions[random.nextInt(actions.length)];
        LocalDateTime timestamp = LocalDateTime.now();
        double value = Math.round(random.nextDouble() * 1000 * 100.0) / 100.0; // Random value 0-1000
        String sessionId = UUID.randomUUID().toString();
        
        return new UserActivity(userId, action, timestamp, value, sessionId);
    }
}