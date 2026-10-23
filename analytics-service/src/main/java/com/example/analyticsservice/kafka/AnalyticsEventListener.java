package com.example.analyticsservice.kafka;

import com.example.analyticsservice.repository.AnalyticsRepository;
import com.example.events.ProductCreatedEvent;
import com.example.events.ProductDeletedEvent;
import com.example.events.UserRegistrationRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AnalyticsEventListener {

    private final AnalyticsRepository repository;

    public AnalyticsEventListener(AnalyticsRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "product.created", groupId = "analytics-service")
    @Transactional
    public void onProductCreated(ProductCreatedEvent event) {
        repository.incrementProducts(event.sellerId());
    }

    @KafkaListener(topics = "product.deleted", groupId = "analytics-service")
    @Transactional
    public void onProductDeleted(ProductDeletedEvent event) {
        repository.decrementProducts(event.sellerId());
    }

    @KafkaListener(topics = "user.registration.requested", groupId = "analytics-service")
    @Transactional
    public void onUserRegistered(UserRegistrationRequestedEvent event) {
        repository.decrementProducts(event.userId());
    }
}