package com.example.productservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record ProductResponse(
        Long productId,
        Long userId,
        Long userProductId,
        String name,
        BigDecimal price,
        Integer count,
        Integer countOfSales,
        BigDecimal averageEstimation
) {}
