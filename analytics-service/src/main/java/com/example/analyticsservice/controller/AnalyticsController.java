package com.example.analyticsservice.controller;

import com.example.analyticsservice.dto.SellerStatsResponse;
import com.example.analyticsservice.entities.SellerStats;
import com.example.analyticsservice.repository.AnalyticsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import com.example.analyticsservice.Service.AnalyticsService;

@RestController
@Validated
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/sellers")
    public List<SellerStatsResponse> getAllSellers() {
        return service.getAllStats();
    }

    @GetMapping("/sellers/{userId}")
    public ResponseEntity<SellerStatsResponse> getSellerStats(@PathVariable @Positive Long userId) {
        return service.getSellerStats(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}