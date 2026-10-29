package com.example.analyticsservice.kafka;

import com.example.analyticsservice.repository.AnalyticsRepository;
import com.example.events.ProductCreatedEvent;
import com.example.events.ProductDeletedEvent;
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
        repository.upsertProduct(
                event.productId(),
                event.sellerId(),
                event.price(),
                event.count(),
                event.countOfSales(),
                event.averageEstimation()
        );
    }

    @KafkaListener(topics = "product.updated", groupId = "analytics-service")
    @Transactional
    public void onProductUpdated(ProductCreatedEvent event) {
        repository.upsertProduct(
                event.productId(),
                event.sellerId(),
                event.price(),
                event.count(),
                event.countOfSales(),
                event.averageEstimation()
        );
    }

    @KafkaListener(topics = "product.deleted", groupId = "analytics-service")
    @Transactional
    public void onProductDeleted(ProductDeletedEvent event) {
        repository.deleteProduct(event.productId(), event.sellerId());
    }
}