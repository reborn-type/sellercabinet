package com.example.userservice.kafka;

import com.example.events.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserEventPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public UserEventPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void publishCreated(com.example.userservice.entities.User u) {
        var event = new UserCreatedEvent(
                u.getUserId(), u.getFirstName(), u.getLastName(), u.getEmail(), Instant.now());
        kafka.send("user.created", String.valueOf(u.getUserId()), event);
    }

    public void publishUpdated(com.example.userservice.entities.User u) {
        kafka.send("user.updated", String.valueOf(u.getUserId()),
                new UserUpdatedEvent(u.getUserId(), u.getFirstName(), u.getLastName(),
                        u.getEmail(), Instant.now()));
    }

    public void publishDeleted(Long userId) {
        kafka.send("user.deleted", String.valueOf(userId),
                new UserDeletedEvent(userId, Instant.now()));
    }
}