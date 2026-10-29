package com.example.productservice.entities;
import com.fasterxml.jackson.databind.BeanProperty;

import java.math.BigDecimal;

public class Product {
    private Long productId;
    private Long sellerId;
    private String name;
    private BigDecimal price;
    private Integer count;
    private Integer countOfSales;
    private BigDecimal averageEstimation;

    public Product() {}

    public Product(Long sellerId, String name, BigDecimal price, Integer count, Integer countOfSales, BigDecimal averageEstimation) {
        this.sellerId = sellerId;
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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
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