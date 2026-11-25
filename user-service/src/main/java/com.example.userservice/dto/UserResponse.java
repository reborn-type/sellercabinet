package com.example.userservice.dto;

public record UserResponse(
        Long userId,
        String firstName,
        String lastName,
        Integer age,
        String email,
        String role,
        String status) {
}
