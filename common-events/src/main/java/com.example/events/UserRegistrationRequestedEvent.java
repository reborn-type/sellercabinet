package com.example.events;

import java.time.Instant;

public record UserRegistrationRequestedEvent(
        Long userId,
        String firstName,
        String lastName,
        int age,
        String email,
        String role,
        Instant occurredAt
) {}