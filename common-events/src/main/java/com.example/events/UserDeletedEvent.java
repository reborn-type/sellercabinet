package com.example.events;

import java.time.Instant;

public record UserDeletedEvent(
        Long sellerId,
        Instant occurredAt
) {}