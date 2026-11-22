package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserCreatedEvent(
        Long sellerId,
        String firstName,
        String lastName,
        String email,
        Instant occurredAt
) {}