package com.example.analyticsservice.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerStats {
    private Long sellerId;
    private Integer totalProducts;
    private Long totalSales;
    private BigDecimal totalStockValue;
    private BigDecimal averageEstimation;
    private LocalDateTime updatedAt;

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

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