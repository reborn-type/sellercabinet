package com.example.sellercabinet.entities;

public class Product {
    private Long product_id; 
    private Long seller_id;
    private String name; 
    private Double price; 
    private Integer count; 
    private Integer count_of_sales; 
    private Double average_estimation;  

    public Product() {}

    public Product(Long seller_id, String name, double price, int count, int count_of_sales, double average_estimation) {
        this.seller_id = seller_id; 
        this.name = name; 
        this.price = price; 
        this.count = count; 
        this.count_of_sales = count_of_sales; 
        this.average_estimation = average_estimation; 
    }

    public void setProductId(Long product_id){
        this.product_id = product_id; 
    }

    public Long getProductId(){
        return this.product_id; 
    }

    public void setSellerId(Long seller_id){
        this.seller_id = seller_id; 
    }

    public Long getSellerId(){
        return this.seller_id;
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

    public void setCountOfSales(int count_of_sales){
        this.count_of_sales = count_of_sales; 
    }
    
    public int getCountOfSales(){
        return this.count_of_sales;
    }

    public void setAverageEstimation(double average_estimation){
        this.average_estimation = average_estimation; 
    }

    public double getAverageEstimation(){
        return this.average_estimation; 
    }
}
