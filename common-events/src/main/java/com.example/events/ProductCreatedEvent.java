package com.example.events;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductCreatedEvent(
        Long productId,
        @JsonAlias("seller_id") Long userId,
        String name,
        BigDecimal price,
        Integer count,
        Long countOfSales,
        BigDecimal averageEstimation,
        Instant occurredAt
) {}