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
    public Seller getSellerById (Long seller_id) {
        return repo.getById(seller_id)
        .orElseThrow(() -> new EntityNotFoundException(
            "Продавец не найден с таким id: " + seller_id
        )); 
    }

    @Transactional
    public List<Product> getProductsBySellerId(Long seller_id){
        return productRepository.findBySellerId(seller_id);
    }

    @Transactional 
    public Seller createSeller(String first_name, String last_name, int age, String email){
        return repo.create(first_name, last_name, age, email);
    }

    @Transactional 
    public void updateSeller(Long id, String first_name, String last_name, int age, String email){
        repo.update(id, first_name, last_name, age, email);
    }

    @Transactional 
    public void deleteSeller(Long id){
        repo.deleteById(id);
    }
}
