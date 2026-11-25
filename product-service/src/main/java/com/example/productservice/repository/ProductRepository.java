package com.example.productservice.repository;

import java.util.List;
import java.util.Optional;

import com.example.productservice.dto.ProductResponse;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.productservice.entities.Product;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query(
            """
            SELECT NEW com.example.productservice.dto.ProductResponse(
                p.productId, p.userId,
                row_number() over (order by p.productId),
                p.name, p.price, p.count, p.countOfSales, p.averageEstimation
            )
            FROM Product p
            ORDER BY p.userId, p.productId
            """
    )
    List<ProductResponse> findAllResponses();

    @Query(
            """
            SELECT NEW com.example.productservice.dto.ProductResponse(
                p.productId, p.userId,
                row_number() over (order by p.productId),
                p.name, p.price, p.count, p.countOfSales, p.averageEstimation
            )
            FROM Product p
            WHERE p.userId = :userId
            ORDER BY p.productId
            """
    )
    List<ProductResponse> findResponseByUserId(@Param("userId") Long userId);

    @Query(
            """
            SELECT NEW com.example.productservice.dto.ProductResponse(
                p.productId, p.userId,
                (SELECT count(previous) FROM Product previous
                WHERE previous.userId = p.userId
                    AND previous.productId <= p.productId),
                p.name, p.price, p.count, p.countOfSales, p.averageEstimation
            )
            FROM Product p
            WHERE p.productId = :productId
            ORDER BY p.productId
            """
    )
    Optional<ProductResponse> findResponseById(@Param("productId") Long productId);


    @Query(
            """
            SELECT COALESCE(sum(p.countOfSales), 0L)
            FROM Product p
            WHERE p.userId = :userId
            """
    )
    Long getSellerSales(@Param("userId") Long userId);


    @Query(
            """
            SELECT COALESCE(sum(p.countOfSales), 0L)
            FROM Product p
            """
    )
    Long getTotalSales();

    @Modifying
    @Transactional
    @Query(
            """
            DELETE FROM Product p WHERE p.userId = :userId
            """
    )
    int deleteAllByUserId(@Param("userId") Long userId);
}
