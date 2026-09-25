package com.example.sellercabinet.entities;

public class Product {
    private Long productId;
    private Long sellerId;
    private String name; 
    private Double price; 
    private Integer count; 
    private Integer countOfSales;
    private Double averageEstimation;

    public Product() {}

    public Product(Long sellerId, String name, double price, int count, int countOfSales, double averageEstimation) {
        this.sellerId = sellerId;
        this.name = name; 
        this.price = price; 
        this.count = count; 
        this.countOfSales = countOfSales;
        this.averageEstimation = averageEstimation;
    }

    public void setProductId(Long product_id){
        this.productId = product_id;
    }

    public Long getProductId(){
        return this.productId;
    }

    public void setSellerId(Long sellerId){
        this.sellerId = sellerId;
    }

    public Long getSellerId(){
        return this.sellerId;
    }

    public void setName(String name){
        this.name = name; 
    }
    
    public String getName(){
        return this.name; 
    }

    public void setPrice(double price){
        this.price = price; 
    }

    public double getPrice(){
        return this.price;
    }

    public void setCount(int count){
        this.count = count; 
    }

    public int getCount(){
        return this.count; 
    }

    public void setCountOfSales(int countOfSales){
        this.countOfSales = countOfSales;
    }
    
    public int getCountOfSales(){
        return this.countOfSales;
    }

    public void setAverageEstimation(double averageEstimation){
        this.averageEstimation = averageEstimation;
    }

    public double getAverageEstimation(){
        return this.averageEstimation;
    }
}
