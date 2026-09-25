package com.example.sellercabinet.entities;

public class Seller {
    private Long sellerId;
    private String firstName;
    private String lastName;
    private int age; 
    private String email;

    public Seller(){}

    public Seller(String firstName, String lastName, int age, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age; 
        this.email = email; 
    }

    public void setSellerId(Long sellerId){
        this.sellerId = sellerId;
    }

    public Long getSellerId(){
        return this.sellerId;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getLastName(){
        return this.lastName;
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