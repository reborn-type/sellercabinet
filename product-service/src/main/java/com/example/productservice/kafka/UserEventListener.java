package com.example.productservice.kafka;

import com.example.productservice.repository.ProductRepository;
import com.example.productservice.repository.SellerProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.events.*;

@Component
public class UserEventListener {
    private final SellerProjectionRepository projections;
    private final ProductRepository products;

    public UserEventListener(SellerProjectionRepository projections, ProductRepository products){
        this.projections = projections;
        this.products = products;
    }

    @KafkaListener(topics = "user.created", groupId = "product-service")
    @Transactional
    public void onUserCreated(UserCreatedEvent e){
        projections.upsert(e.sellerId(), e.firstName(), e.lastName(), e.email());
    }

    @KafkaListener(topics = "user.updated", groupId = "product-service")
    @Transactional
    public void onUserUpdated(UserUpdatedEvent e){
        projections.upsert(e.sellerId(), e.firstName(), e.lastName(), e.email());
    }

    @KafkaListener(topics = "user.deleted", groupId = "product-service")
    @Transactional
    public void onUserDeleted(UserDeletedEvent e){
        projections.delete(e.sellerId());
        products.deleteAllBySeller(e.sellerId());
    }

}
