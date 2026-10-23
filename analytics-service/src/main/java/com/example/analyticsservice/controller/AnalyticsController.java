package com.example.analyticsservice.controller;

import com.example.analyticsservice.entities.SellerStats;
import com.example.analyticsservice.repository.AnalyticsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsRepository repository;

    public AnalyticsController(AnalyticsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/sellers")
    public List<SellerStats> getAllSellers() {
        return repository.getAllStats();
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<SellerStats> getSellerStats(@PathVariable Long sellerId) {
        SellerStats stats = repository.getStatsBySellerId(sellerId);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }
}