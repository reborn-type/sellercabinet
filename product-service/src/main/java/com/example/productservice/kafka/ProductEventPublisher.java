package com.example.productservice.kafka;

import com.example.events.ProductCreatedEvent;
import com.example.events.ProductDeletedEvent;
import com.example.productservice.entities.Product;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProductEventPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public ProductEventPublisher(KafkaTemplate<String, Object> kafka){
        this.kafka = kafka;
    }

    public void publishCreated(Product p){
        kafka.send("product.created", String.valueOf(p.getUserId()),
                new ProductCreatedEvent(p.getProductId(), p.getUserId(), p.getName(),
                        p.getPrice(), p.getCount(), p.getCountOfSales().longValue(), p.getAverageEstimation(), Instant.now()));
    }

    public void publishUpdated(Product p) {
        kafka.send("product.updated", String.valueOf(p.getUserId()),
                new ProductCreatedEvent(p.getProductId(), p.getUserId(), p.getName(),
                        p.getPrice(), p.getCount(), p.getCountOfSales().longValue(), p.getAverageEstimation(), Instant.now()));
    }

    public void publishDeleted(Long productId, Long userId) {
        kafka.send("product.deleted", String.valueOf(userId),
                new ProductDeletedEvent(productId, userId, Instant.now()));
    }
}
