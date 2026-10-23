package com.example.authservice.kafka;

import com.example.events.UserRegistrationRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


import java.time.Instant;


@Component
public class AuthEventPublisher {

    public static final String TOPIC_REGISTRATION = "user.registration.requested";

    private final KafkaTemplate<String, Object> kafka;

    public AuthEventPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void publishRegistrationRequested(Long userId, String firstName, String lastName,
                                             int age, String email, String role){
        var event = new UserRegistrationRequestedEvent(userId, firstName, lastName, age, email, role, Instant.now());
        kafka.send(TOPIC_REGISTRATION, String.valueOf(userId), event);
    }
}
