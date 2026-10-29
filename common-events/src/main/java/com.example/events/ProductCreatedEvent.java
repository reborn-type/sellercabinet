package com.example.events;

import java.time.Instant;

import java.math.BigDecimal;

public record ProductCreatedEvent(
        Long productId,
        Long sellerId,
        String name,
        BigDecimal price,
        Instant occurredAt
) {}