package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserRegistrationRequestedEvent(
        Long userId,
        String firstName,
        String lastName,
        int age,
        String email,
        String role,
        Instant occurredAt
) {}