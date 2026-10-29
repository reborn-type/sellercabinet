package com.example.productservice.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(

        @NotBlank(message = "Название товара обазятельно")
        @Size(min=2, max=200, message = "Название товара содержит от 2 до 200 символов")
        String name,

        @NotNull(message = "Стоимость товара обязательна")
        @DecimalMin(value = "0.01", message = "Стоимость товара должна быть больше 0")
        @Digits(
                integer = 12,
                fraction = 2,
                message = "Количество символов до зяпятой не более 12 и после не более 2"
        )
        BigDecimal price,

        @NotNull(message = "Количество товара обязательно")
        @PositiveOrZero(message = "Количество товара не может быть отрицательным")
        Integer count,

        @NotNull(message = "Количество продаж обязательно")
        @PositiveOrZero(message = "Количество продаж не может быть отрецательным")
        Integer countOfSales,

        @NotNull(message = "Средняя оценка обязательна")
        @DecimalMin(value = "1.0", message = "Средняя оценка не может быть меньше 1.0")
        @DecimalMax(value = "5.0", message = "Средняя оценка не можеть быть больше 5.0")
        BigDecimal averageEstimation
) {}