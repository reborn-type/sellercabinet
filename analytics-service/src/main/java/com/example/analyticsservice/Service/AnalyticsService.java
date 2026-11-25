package com.example.analyticsservice.Service;

import com.example.analyticsservice.dto.SellerStatsResponse;
import com.example.analyticsservice.repository.AnalyticsRepository;
import com.example.events.ProductCreatedEvent;
import com.example.events.ProductDeletedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public List<SellerStatsResponse> getAllStats() {
        return repository.getAllStats();
    }

    public Optional<SellerStatsResponse> getSellerStats(Long userId){
        return repository.getStatsByUserId(userId);
    }

    @Transactional
    public void upsertProduct(ProductCreatedEvent event) {
        Long userId = event.userId();

        repository.ensureSellerStats(userId);
        repository.lockSellerStats(userId);

        repository.upsertProduct(
                event.productId(),
                userId,
                event.price(),
                event.count(),
                event.countOfSales(),
                event.averageEstimation()
        );

        repository.recalculateSellerStats(userId);

    }

    @Transactional
    public void deleteProduct(ProductDeletedEvent event) {
        Long userId = event.userId();

        repository.ensureSellerStats(userId);
        repository.lockSellerStats(userId);

        repository.deleteProduct(event.productId(), userId);
        repository.recalculateSellerStats(userId);
    }

}
