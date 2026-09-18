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

    public List<Product> getAllProduct(){
        return repo.getAll();
    }

    @Transactional
    public Product getProductById(Long product_id){
        return repo.getById(product_id)
        .orElseThrow(() -> new EntityNotFoundException(
            "Товар с таким id не найден" + product_id
        ));
    }

    @Transactional
    public Product getProductBySellerAndProductId(Long seller_id, Long product_id){
        return repo.findByProductAndSellerId(seller_id, product_id);
    }

    @Transactional 
    public List<Product> getProductsFromSeller(Long seller_id) {
        return repo.findBySellerId(seller_id);
    }

    @Transactional 
    public Product createProduct(Long seller_id, String name, double price, int count, int count_of_sales, double average_estimation){
        return repo.create(seller_id, name, price, count, count_of_sales, average_estimation);
    }

    @Transactional 
    public void updateProduct(Long product_id, Long seller_id, String name, double price, int count, int count_of_sales, double average_estimation){
        repo.update(product_id, seller_id, name, price, count, count_of_sales, average_estimation);
    }

    @Transactional
    public void deleteProduct(Long seller_id, Long product_id){
        repo.deleteById(seller_id, product_id);
    }
}
