package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductDeletedEvent(
        Long productId,
        Long sellerId,
        Instant occurredAt
) {}
