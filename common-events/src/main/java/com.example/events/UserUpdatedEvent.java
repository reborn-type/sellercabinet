package com.example.events;

import java.time.Instant;

public record UserUpdatedEvent(
        Long sellerId,
        String firstName,
        String lastName,
        String email,
        Instant occurredAt
) {}