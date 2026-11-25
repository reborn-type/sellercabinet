package com.example.userservice.dto;
import jakarta.validation.constraints.*;


public record CreateUserRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max=50, message = "Имя должно содержать от 2 до 50 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
        String lastName,

        @NotNull(message = "Возраст обязателен")
        @Min(value = 18, message = "Возраст должен быть не менее 18 лет")
        @Max(value = 110, message = "Возраст не должен превышать 110 лет")
        Integer age,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Неккоректный email")
        String email
) {}