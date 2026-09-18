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

    @GetMapping("/{seller_id}") 
    public ResponseEntity<Seller> getSellerById(
        @PathVariable Long seller_id 
    ){
        return ResponseEntity.ok(sellerService.getSellerById(seller_id));
    }


    @GetMapping("/{seller_id}/products")
    public ResponseEntity<List<Product>> GetProductsBySellerId(@PathVariable Long seller_id)
    {
        List<Product> products = sellerService.getProductsBySellerId(seller_id);
        return ResponseEntity.ok(products); 
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody CreateSellerRequest req){
        Seller created = sellerService.createSeller(req.first_name(), req.last_name(), req.age(), req.email());
        URI location = URI.create("/api/sellers/" + created.getSellerId());
        return ResponseEntity.created(location).body(created); 
    }

    @PostMapping("/{seller_id}/products")
    public ResponseEntity <Product> createProductForSeller(
        @PathVariable Long seller_id, 
        @RequestBody CreateProductRequest req
    ) {
        Product created = productService.createProduct(
                seller_id,
                req.name(),
                req.price(),
                req.count(),
                req.count_of_sales(),
                req.average_estimation()
        );
        URI location = URI.create("/api/sellers/" + seller_id + "/products/" + created.getProductId());
        return ResponseEntity.created(location).body(created);
    } 

    @GetMapping("/{seller_id}/product/{product_id}")
    public ResponseEntity<Product> getProductOfSeller(
        @PathVariable Long seller_id,
        @PathVariable Long product_id 
    ) {
        Product product = productService.getProductBySellerAndProductId(seller_id, product_id);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/{seller_id}/product/{product_id}")
    public ResponseEntity<Void> updateProductOfSeller(
        @PathVariable Long seller_id,
        @PathVariable Long product_id,
        @RequestBody UpdateProductRequest req
    ) {
        productService.updateProduct(product_id, seller_id, req.name(), req.price(), 
        req.count(), req.count_of_sales(), req.average_estimation());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{seller_id}") 
    public ResponseEntity<Void> updateSellerById(
        @PathVariable Long seller_id,
        @RequestBody UpdateSellerRequest req
    ) {
        sellerService.updateSeller(seller_id, req.first_name(), req.last_name(), req.age(), req.email());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{seller_id}")
    public ResponseEntity<Void> deleteSellerById(@PathVariable Long seller_id)
    {
        sellerService.deleteSeller(seller_id);
        return ResponseEntity.noContent().build(); 
    }

    @DeleteMapping("/{seller_id}/product/{product_id}")
    public ResponseEntity<Void> deleteProductOfSeller(
        @PathVariable Long seller_id, 
        @PathVariable Long product_id 
    ) {
        productService.deleteProduct(seller_id, product_id);
        return ResponseEntity.noContent().build();
    }
}

record CreateSellerRequest(String first_name, String last_name, int age, String email) {} 
record UpdateSellerRequest(String first_name, String last_name, int age, String email) {}
record UpdateProductRequest(String name, Double price, Integer count, Integer count_of_sales, Double average_estimation) {}
record CreateProductRequest(String name, Double price, Integer count, Integer count_of_sales, Double average_estimation) {}