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
        kafka.send("product.created", String.valueOf(p.getSellerId()),
                new ProductCreatedEvent(p.getProductId(), p.getSellerId(), p.getName(),
                        p.getPrice(), Instant.now()));
    }

    public void publishUpdated(Product p) {
        kafka.send("product.updated", String.valueOf(p.getSellerId()),
                new ProductCreatedEvent(p.getProductId(), p.getSellerId(), p.getName(),
                        p.getPrice(), Instant.now()));
    }

    public void publishDeleted(Long productId, Long sellerId) {
        kafka.send("product.deleted", String.valueOf(sellerId),
                new ProductDeletedEvent(productId, sellerId, Instant.now()));
    }
}
