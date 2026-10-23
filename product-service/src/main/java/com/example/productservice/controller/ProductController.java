package com.example.productservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.example.productservice.entities.Product;
import com.example.productservice.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/by-seller/{sellerId}")
    public ResponseEntity<List<Product>> bySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    @GetMapping("/by-seller/{sellerId}/sales")
    public ResponseEntity<Long> sellerSales(@PathVariable Long sellerId) {
        return ResponseEntity.ok(productService.getSellerSales(sellerId));
    }

    @GetMapping("/sales/total")
    public ResponseEntity<Long> totalSales() {
        return ResponseEntity.ok(productService.getTotalSales());
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Product> create(@RequestBody CreateProductRequest req,
                                          @AuthenticationPrincipal Jwt jwt) {
        Long sellerId = Long.valueOf(jwt.getSubject());
        Product created = productService.createProduct(sellerId, req.name(), req.price(),
                req.count(), req.countOfSales(), req.averageEstimation());
        return ResponseEntity.created(URI.create("/api/products/" + created.getProductId()))
                .body(created);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long productId,
                                       @RequestBody UpdateProductRequest req,
                                       @AuthenticationPrincipal Jwt jwt) {
        Long callerId = Long.valueOf(jwt.getSubject());
        boolean isAdmin = jwt.getClaimAsStringList("roles").contains("ADMIN");
        productService.updateProduct(productId, callerId, isAdmin,
                req.name(), req.price(), req.count(), req.countOfSales(), req.averageEstimation());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long productId,
                                       @AuthenticationPrincipal Jwt jwt) {
        Long callerId = Long.valueOf(jwt.getSubject());
        boolean isAdmin = jwt.getClaimAsStringList("roles").contains("ADMIN");
        productService.deleteProduct( productId, callerId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}

record CreateProductRequest(String name, Double price, Integer count,
                            Integer countOfSales, Double averageEstimation) {}
record UpdateProductRequest(String name, Double price, Integer count,
                            Integer countOfSales, Double averageEstimation) {}