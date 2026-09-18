package com.example.sellercabinet.entities;

public class Seller {
    private Long seller_id; 
    private String first_name; 
    private String last_name; 
    private int age; 
    private String email;

    public Seller(){}

    public Seller(String first_name, String last_name, int age, String email) {
        this.first_name = first_name;
        this.last_name = last_name; 
        this.age = age; 
        this.email = email; 
    }

    public void setSellerId(Long seller_id){
        this.seller_id = seller_id; 
    }

    public Long getSellerId(){
        return this.seller_id; 
    }

    public void setFirstName(String first_name){
        this.first_name = first_name;
    }

    public String getFirstName() {
        return this.first_name; 
    }

    public void setLastName(String last_name){
        this.last_name = last_name; 
    }

    public String getLastName(){
        return this.last_name;
    }

    public void setAge(int age){
        this.age = age; 
    }

    public int getAge(){
        return this.age; 
    }

    public void setEmail(String email){
        this.email = email; 
    }

    public String getEmail() {
        return this.email;
    }


}