package com.example.productservice.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.productservice.entities.Product;
import com.example.productservice.kafka.ProductEventPublisher;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.repository.SellerProjectionRepository;
import com.example.productservice.dto.ProductResponse;
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

    public List<ProductResponse> getAllProducts() {
        return repo.findAllResponses();
    }

    public ProductResponse getProductById(Long productId) {
        return repo.findResponseById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден: " + productId));
    }

    public List<ProductResponse> getProductsBySeller(Long userId) {
        return repo.findResponseBySellerId(userId);
    }

    public Long getSellerSales(Long userId) {
        return repo.getSellerSales(userId);
    }

    public Long getTotalSales() {
        return repo.getTotalSales();
    }

    @Transactional
    public ProductResponse createProduct(
            Long userId,
            String name,
            BigDecimal price,
            Integer count,
            Integer countOfSales,
            BigDecimal averageEstimation
    ){
        if(!sellers.exists(userId)){
            throw new EntityNotFoundException("Продавец не найден: " + userId);
        }

        Product product = new Product(
                userId,
                name,
                price,
                count,
                countOfSales,
                averageEstimation
        );

        Product created = repo.save(product);

        ProductResponse response = getProductById(created.getProductId());

        afterCommit(() -> publisher.publishCreated(created));

        return response;
    }

    @Transactional
    public void updateProduct(
            Long productId,
            Long callerId,
            Boolean isAdmin,
            String name,
            BigDecimal price,
            Integer count,
            Integer countOfSales,
            BigDecimal averageEstimation
    ) {
        Product product = findEntityById(productId);

        checkAccess(product, callerId, isAdmin);

        product.setName(name);
        product.setPrice(price);
        product.setCount(count);
        product.setCountOfSales(countOfSales);
        product.setAverageEstimation(averageEstimation);

        afterCommit(() -> publisher.publishUpdated(product));
    }

    @Transactional
    public void deleteProduct(
            Long productId,
            Long callerId,
            boolean isAdmin
    ){
        Product product = findEntityById(productId);

        checkAccess(product, callerId, isAdmin);

        Long sellerId = product.getSellerId();

        repo.delete(product);

        afterCommit(() -> publisher.publishDeleted(productId, sellerId));
    }

    private void checkAccess(
            Product product,
            Long callerId,
            boolean isAdmin
    ){
        if (!isAdmin && !product.getSellerId().equals(callerId)) {
            throw new AccessDeniedException(
                    "Товар принадлежит другому продавцу");
        }
    }

    private Product findEntityById(Long productId){
        return repo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден: " + productId));
    }

    private void afterCommit(Runnable action){
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit(){
                        action.run();
                    }
                }
        );
    }

}