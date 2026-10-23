package com.example.events;

import java.time.Instant;

public record ProductCreatedEvent(
        Long productId,
        Long sellerId,
        String name,
        Double price,
        Instant occurredAt
) {}