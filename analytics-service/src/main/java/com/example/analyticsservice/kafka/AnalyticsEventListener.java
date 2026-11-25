package com.example.analyticsservice.kafka;

import com.example.analyticsservice.Service.AnalyticsService;
import com.example.events.ProductCreatedEvent;
import com.example.events.ProductDeletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsEventListener {

    private final AnalyticsService service;

    public AnalyticsEventListener(AnalyticsService service) {
        this.service = service;
    }

    @KafkaListener(topics = "product.created", groupId = "analytics-service")
    public void onProductCreated(ProductCreatedEvent event) {
        service.upsertProduct(event);
    }

    @KafkaListener(topics = "product.updated", groupId = "analytics-service")
    public void onProductUpdated(ProductCreatedEvent event) {
        service.upsertProduct(event);
    }

    @KafkaListener(topics = "product.deleted", groupId = "analytics-service")
    public void onProductDeleted(ProductDeletedEvent event) {
        service.deleteProduct(event);
    }
}
