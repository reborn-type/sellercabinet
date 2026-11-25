package com.example.productservice.entities;
import com.fasterxml.jackson.databind.BeanProperty;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.persistence.*;

@Table(name = "products")
@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Transient
    private Long userProductId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "count_of_sales", nullable = false)
    private Integer countOfSales;

    @Column(name = "average_estimation", nullable = false)
    private BigDecimal averageEstimation;

    public Product() {}

    public Product(Long userId, String name, BigDecimal price, Integer count, Integer countOfSales, BigDecimal averageEstimation) {
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.countOfSales = countOfSales;
        this.averageEstimation = averageEstimation;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserProductId() {
        return userProductId;
    }

    public void setUserProductId(Long userProductId) {
        this.userProductId = userProductId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public BigDecimal getPrice(){
        return this.price;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public Integer getCount(){
        return this.count;
    }

    public void setCount(Integer count){
        this.count = count;
    }

    public Integer getCountOfSales(){
        return this.countOfSales;
    }

    public void setCountOfSales(Integer countOfSales){
        this.countOfSales = countOfSales;
    }

    public BigDecimal getAverageEstimation(){
        return this.averageEstimation;
    }

    public void setAverageEstimation(BigDecimal averageEstimation){
        this.averageEstimation = averageEstimation;
    }

}
