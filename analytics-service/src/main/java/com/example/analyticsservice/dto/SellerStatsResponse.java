package com.example.analyticsservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SellerStatsResponse (
        Long userId,
        Integer totalProducts,
        Long totalSales,
        BigDecimal totalStockValue,
        BigDecimal averageEstimation,
        LocalDateTime updatedAt
        ) {
}
