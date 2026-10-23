package com.example.events;

import java.time.Instant;

public record ProductDeletedEvent(
        Long productId,
        Long sellerId,
        Instant occurredAt
) {}
