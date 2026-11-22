package com.example.productservice.entities;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.persistence.*;

import java.time.Instant;


@Table(name = "sellers_projection")
@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SellerProjection {

    public SellerProjection(
            Long userId,
            String firstName,
            String lastName,
            String email,
            Instant updatedAt
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = updatedAt;
    }

    public SellerProjection() {}

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    public Long getUserId(){
        return this.userId;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getEmail(){
        return this.email;
    }

    public Instant getUpdatedAt(){
        return this.updatedAt;
    }
}
