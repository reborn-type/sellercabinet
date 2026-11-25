package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserUpdatedEvent(
        @JsonAlias("seller_id") Long userId,
        String firstName,
        String lastName,
        String email,
        Instant occurredAt
) {}