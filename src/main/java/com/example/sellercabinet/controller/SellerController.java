package com.example.sellercabinet.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sellercabinet.entities.Product;
import com.example.sellercabinet.entities.Seller;
import com.example.sellercabinet.service.ProductService;
import com.example.sellercabinet.service.SellerService;

@RestController 
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService; 
    private final ProductService productService;
    
    public SellerController(SellerService sellerService, ProductService productService){
        this.sellerService = sellerService; 
        this.productService = productService; 
    }

    @GetMapping
    public List<Seller> getAll(){
        return sellerService.getAllSellers();
    }

    @GetMapping("/products")
    public List<Product> getProducts(){return productService.getAllProducts();}

    @GetMapping("/{sellerId}")
    public ResponseEntity<Seller> getSellerById(
        @PathVariable Long sellerId
    ){
        return ResponseEntity.ok(sellerService.getSellerById(sellerId));
    }

    @GetMapping("/{sellerId}/products")
    public ResponseEntity<List<Product>> GetProductsBySellerId(@PathVariable Long sellerId)
    {
        List<Product> products = sellerService.getProductsBySellerId(sellerId);
        return ResponseEntity.ok(products); 
    }

    @GetMapping("/{sellerId}/products/sales")
    public ResponseEntity<Long> GetSellerSales(@PathVariable Long sellerId)
    {
        return ResponseEntity.ok(sellerService.getSalesFromSeller(sellerId));
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody CreateSellerRequest req){
        Seller created = sellerService.createSeller(req.firstName(), req.lastName(), req.age(), req.email());
        URI location = URI.create("/api/sellers/" + created.getSellerId());
        return ResponseEntity.created(location).body(created); 
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity <Product> createProductForSeller(
        @PathVariable Long sellerId,
        @RequestBody CreateProductRequest req
    ) {
        Product created = productService.createProduct(
                sellerId,
                req.name(),
                req.price(),
                req.count(),
                req.countOfSales(),
                req.averageEstimation()
        );
        URI location = URI.create("/api/sellers/" + sellerId + "/products/" + created.getProductId());
        return ResponseEntity.created(location).body(created);
    } 

    @GetMapping("/products/sales")
    public ResponseEntity<Long> getAllProductSales(){
        return ResponseEntity.ok(productService.getTotalProductSales());
    }

    @GetMapping("/{sellerId}/product/{productId}")
    public ResponseEntity<Product> getProductOfSeller(
        @PathVariable Long sellerId,
        @PathVariable Long productId
    ) {
        Product product = productService.getProductBySellerAndProductId(sellerId, productId);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/{sellerId}/product/{productId}")
    public ResponseEntity<Void> updateProductOfSeller(
        @PathVariable Long sellerId,
        @PathVariable Long productId,
        @RequestBody UpdateProductRequest req
    ) {
        productService.updateProduct(productId, sellerId, req.name(), req.price(),
        req.count(), req.countOfSales(), req.averageEstimation());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<Void> updateSellerById(
        @PathVariable Long sellerId,
        @RequestBody UpdateSellerRequest req
    ) {
        sellerService.updateSeller(sellerId, req.firstName(), req.lastName(), req.age(), req.email());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sellerId}")
    public ResponseEntity<Void> deleteSellerById(@PathVariable Long sellerId)
    {
        sellerService.deleteSeller(sellerId);
        return ResponseEntity.noContent().build(); 
    }

    @DeleteMapping("/{sellerId}/product/{productId}")
    public ResponseEntity<Void> deleteProductOfSeller(
        @PathVariable Long sellerId,
        @PathVariable Long productId
    ) {
        productService.deleteProduct(sellerId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProductById(
            @PathVariable Long productId
    ){
        productService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}

record CreateSellerRequest(String firstName, String lastName, int age, String email) {}
record UpdateSellerRequest(String firstName, String lastName, int age, String email) {}
record UpdateProductRequest(String name, Double price, Integer count, Integer countOfSales, Double averageEstimation) {}
record CreateProductRequest(String name, Double price, Integer count, Integer countOfSales, Double averageEstimation) {}