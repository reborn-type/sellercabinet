package com.example.sellercabinet.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sellercabinet.entities.Product;
import com.example.sellercabinet.entities.Seller;
import com.example.sellercabinet.repository.ProductRepository;
import com.example.sellercabinet.repository.SellerRepository;

import jakarta.persistence.EntityNotFoundException;


@Service 
@Transactional(readOnly=true)
public class SellerService {
    private final ProductRepository productRepository;
    private final SellerRepository repo;
    public SellerService(SellerRepository repo, ProductRepository productRepository) {
        this.repo = repo;
        this.productRepository = productRepository; 
    }

    public List<Seller> getAllSellers (){
        return repo.getAll();
    }

    @Transactional 
    public Seller getSellerById (Long sellerId) {
        return repo.getById(sellerId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Продавец не найден с таким id: " + sellerId
        )); 
    }

    @Transactional
    public List<Product> getProductsBySellerId(Long sellerId){
        return productRepository.findBySellerId(sellerId);
    }

    @Transactional 
    public Seller createSeller(String firstName, String lastName, int age, String email){
        return repo.create(firstName, lastName, age, email);
    }

    @Transactional 
    public void updateSeller(Long sellerId, String firstName, String lastName, int age, String email){
        repo.update(sellerId, firstName, lastName, age, email);
    }

    @Transactional 
    public void deleteSeller(Long sellerId){
        repo.deleteById(sellerId);
    }

    @Transactional
    public Long getSalesFromSeller(Long sellerId){
        return repo.getSellerSales(sellerId);
    }
}
