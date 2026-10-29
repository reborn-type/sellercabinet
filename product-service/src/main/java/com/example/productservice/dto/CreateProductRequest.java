package com.example.productservice.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record CreateProductRequest(

        @NotBlank(message = "Название товара обязательно")
        @Size(min = 2, max = 200, message = "Название должно быть от 2 до 200 символов")
        String name,

        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
        @Digits (
                integer = 12,
                fraction = 2,
                message = "Цена должна иметь не более 12 символов до запятой и 2 после"
        )
        BigDecimal price,

        @NotNull(message = "Количество обязательно")
        @PositiveOrZero(message = "Количество не может быть отрицательным")
        Integer count,

        @NotNull(message = "Количество продаж обязательно")
        @PositiveOrZero(message = "Количество продаж не может быть отрицательным")
        Integer countOfSales,

        @NotNull(message = "Средняя оценка обязательна")
        @DecimalMin(value = "1.0", message = "Средняя оценка не может быть меньше 1.0")
        @DecimalMax(value = "5.0", message = "Средняя оценка не может быть больше 5.0")
        BigDecimal averageEstimation
) {}