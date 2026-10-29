package com.example.authservice.dto;
import jakarta.validation.constraints.*;



public record RegisterRequest (

        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
        String lastName,

        @NotNull(message = "Возраст обязателен")
        @Min(value = 18, message = "Возраст должен быть не меньше 18")
        @Max(value = 110, message = "Возраст должен быть не больше 110")
        Integer age,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Неккоректный формат email")
        @Size(max = 200,  message = "Email слишком длинный")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 72, message = "Пароль должен содержать от 8 до 72 символов")
        String password
) {}