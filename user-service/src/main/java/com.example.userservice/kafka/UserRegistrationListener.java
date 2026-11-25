package com.example.userservice.kafka;

import com.example.events.UserRegistrationRequestedEvent;
import com.example.userservice.service.UserRegistrationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener {

    private final UserRegistrationService registrations;

    public UserRegistrationListener(
            UserRegistrationService registrations
    ) {
        this.registrations = registrations;
    }

    @KafkaListener(
            topics = "user.registration.requested",
            groupId = "user-service"
    )
    public void onRegistration(UserRegistrationRequestedEvent event) {
        registrations.registerProfile(event);
    }
}