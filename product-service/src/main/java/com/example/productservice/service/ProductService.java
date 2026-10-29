package com.example.productservice.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.productservice.entities.Product;
import com.example.productservice.kafka.ProductEventPublisher;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.repository.SellerProjectionRepository;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repo;
    private final SellerProjectionRepository sellers;
    private final ProductEventPublisher publisher;

    public ProductService(ProductRepository repo,
                          SellerProjectionRepository sellers,
                          ProductEventPublisher publisher) {
        this.repo = repo;
        this.sellers = sellers;
        this.publisher = publisher;
    }

    public List<Product> getAllProducts() {
        return repo.getAll();
    }

    public Product getProductById(Long productId) {
        return repo.getById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден: " + productId));
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return repo.findBySellerId(sellerId);
    }

    public Long getSellerSales(Long sellerId) {
        return repo.getSellerSales(sellerId);
    }

    public Long getTotalSales() {
        return repo.getTotalSales();
    }

    @Transactional
    public Product createProduct(Long sellerId, String name, BigDecimal price, Integer count,
                                 Integer countOfSales, BigDecimal averageEstimation) {
        if (!sellers.exists(sellerId)) {
            throw new EntityNotFoundException("Продавец не найден или заблокирован: " + sellerId);
        }

        Product created = repo.create(sellerId, name, price, count, countOfSales, averageEstimation);
        publisher.publishCreated(created);
        return created;
    }

    @Transactional
    public void updateProduct(Long productId, Long callerId, boolean isAdmin,
                              String name, BigDecimal price, Integer count,
                              Integer countOfSales, BigDecimal averageEstimation) {
        Product existing = getProductById(productId);

        if (!isAdmin && !existing.getSellerId().equals(callerId)) {
            throw new AccessDeniedException("Товар принадлежит другому продавцу");
        }

        repo.update(productId, existing.getSellerId(), name, price, count, countOfSales, averageEstimation);
        publisher.publishUpdated(getProductById(productId));
    }

    @Transactional
    public void deleteProduct(Long productId, Long callerId, boolean isAdmin) {
        Product existing = getProductById(productId);

        if (!isAdmin && !existing.getSellerId().equals(callerId)) {
            throw new AccessDeniedException("Товар принадлежит другому продавцу");
        }

        repo.deleteById(productId, existing.getSellerId());

        publisher.publishDeleted(productId, existing.getSellerId());
    }
}