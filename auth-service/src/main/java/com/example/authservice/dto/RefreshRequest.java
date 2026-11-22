package com.example.authservice.dto;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RefreshRequest(

        @NotBlank(message = "Refresh токен обязателен")
        String refreshToken
) {}
