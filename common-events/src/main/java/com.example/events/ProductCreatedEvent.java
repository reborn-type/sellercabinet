package com.example.events;

import java.time.Instant;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductCreatedEvent(
        Long productId,
        Long sellerId,
        String name,
        BigDecimal price,
        Integer count,
        Integer countOfSales,
        BigDecimal averageEstimation,
        Instant occurredAt
) {}