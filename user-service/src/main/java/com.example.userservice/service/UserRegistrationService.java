package com.example.userservice.service;

import com.example.events.UserRegistrationRequestedEvent;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository users;

    public UserRegistrationService(UserRepository users) {
        this.users = users;
    }

    @Transactional
    public void registerProfile(UserRegistrationRequestedEvent event) {
        if (event.userId() == null || event.userId() <= 0) {
            throw new IllegalArgumentException(
                    "В событии отсутствует корректный userId"
            );
        }

        int inserted = users.insertFromRegistration(
                event.userId(),
                event.firstName(),
                event.lastName(),
                event.age(),
                event.email(),
                event.role()
        );

        if (inserted == 0 && !users.existsById(event.userId())) {
            throw new IllegalStateException(
                    "Регистрация не сохранена: email уже занят другим профилем"
            );
        }
    }
}