package com.example.authservice.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest(

        @NotBlank(message = "Email обязателен")
        @Email(message = "Неккоректный формат Email")
        String email,

        @NotBlank(message = "Пароль обязателен")
        String password
) {}