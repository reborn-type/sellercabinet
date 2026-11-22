package com.example.productservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;


import com.example.productservice.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;


@RestController
@Validated
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(@PathVariable @Positive Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/by-seller/{userId}")
    public ResponseEntity<List<ProductResponse>> bySeller(@PathVariable @Positive  Long userId) {
        return ResponseEntity.ok(productService.getProductsBySeller(userId));
    }

    @GetMapping("/by-seller/{userId}/sales")
    public ResponseEntity<Long> sellerSales(@PathVariable @Positive  Long userId) {
        return ResponseEntity.ok(productService.getSellerSales(userId));
    }

    @GetMapping("/sales/total")
    public ResponseEntity<Long> totalSales() {
        return ResponseEntity.ok(productService.getTotalSales());
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        ProductResponse created = productService.createProduct(userId, req.name(), req.price(),
                req.count(), req.countOfSales(), req.averageEstimation());
        return ResponseEntity.created(URI.create("/api/products/" + created.productId()))
                .body(created);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> update(@PathVariable @Positive Long productId,
                                       @Valid @RequestBody UpdateProductRequest req,
                                       @AuthenticationPrincipal Jwt jwt) {
        Long callerId = Long.valueOf(jwt.getSubject());
        boolean isAdmin = jwt.getClaimAsStringList("roles").contains("ADMIN");
        productService.updateProduct(productId, callerId, isAdmin,
                req.name(), req.price(), req.count(), req.countOfSales(), req.averageEstimation());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long productId,
                                       @AuthenticationPrincipal Jwt jwt) {
        Long callerId = Long.valueOf(jwt.getSubject());
        boolean isAdmin = jwt.getClaimAsStringList("roles").contains("ADMIN");
        productService.deleteProduct( productId, callerId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
