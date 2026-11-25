package com.example.analyticsservice.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import jakarta.persistence.*;

@Entity
@Table(name = "seller_stats")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SellerStats {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_products")
    private Integer totalProducts;

    @Column(name = "total_sales")
    private Long totalSales;

    @Column(name = "total_stock_value")
    private BigDecimal totalStockValue;

    @Column(name = "average_estimation")
    private BigDecimal averageEstimation;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }

    public Long getTotalSales() { return totalSales; }
    public void setTotalSales(Long totalSales) { this.totalSales = totalSales; }

    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }

    public BigDecimal getAverageEstimation() { return averageEstimation; }
    public void setAverageEstimation(BigDecimal averageEstimation) { this.averageEstimation = averageEstimation; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}