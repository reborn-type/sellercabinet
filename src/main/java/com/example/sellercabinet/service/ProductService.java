package com.example.sellercabinet.service;

import java.util.List; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sellercabinet.entities.Product;
import com.example.sellercabinet.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;


@Service 
@Transactional(readOnly=true)
public class ProductService {
    private final ProductRepository repo; 

    public ProductService(ProductRepository repo){
        this.repo = repo; 
    }

    public List<Product> getAllProducts(){
        return repo.getAll();
    }

    public Long getTotalProductSales() {return repo.getTotalSales();}

    @Transactional
    public Product getProductById(Long productId){
        return repo.getById(productId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Товар с таким id не найден" + productId
        ));
    }

    @Transactional
    public Product getProductBySellerAndProductId(Long sellerId, Long productId){
        return repo.findByProductAndSellerId(sellerId, productId);
    }

    @Transactional 
    public List<Product> getProductsFromSeller(Long sellerId) {
        return repo.findBySellerId(sellerId);
    }

    @Transactional 
    public Product createProduct(Long sellerId, String name, double price, int count, int countOfSales, double averageEstimation){
        return repo.create(sellerId, name, price, count, countOfSales, averageEstimation);
    }

    @Transactional 
    public void updateProduct(Long productId, Long sellerId, String name, double price, int count, int countOfSales, double averageEstimation){
        repo.update(productId, sellerId, name, price, count, countOfSales, averageEstimation);
    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId){
        repo.deleteById(sellerId, productId);
    }

    @Transactional
    public void deleteByProductId(Long productId) {repo.deleteByProductId(productId);}
}
