package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductDeletedEvent(
        Long productId,
        @JsonAlias("seller_id") Long userId,
        Instant occurredAt
) {}
