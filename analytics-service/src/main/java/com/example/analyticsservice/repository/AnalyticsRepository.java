package com.example.analyticsservice.repository;

import com.example.analyticsservice.dto.SellerStatsResponse;
import com.example.analyticsservice.entities.SellerStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.analyticsservice.entities.SellerStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<SellerStats, Long> {


    @Query("""
            select new com.example.analyticsservice.dto.SellerStatsResponse(
                s.userId, s.totalProducts, s.totalSales,
                s.totalStockValue, s.averageEstimation, s.updatedAt
            )
            from SellerStats s
            order by s.totalSales desc
            """)
    List<SellerStatsResponse> getAllStats();

    @Query("""
            select new com.example.analyticsservice.dto.SellerStatsResponse(
                s.userId, s.totalProducts, s.totalSales,
                s.totalStockValue, s.averageEstimation, s.updatedAt
            )
            from SellerStats s
            where s.userId = :userId
            """)
    Optional<SellerStatsResponse> getStatsByUserId(@Param("userId") Long userId);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            insert into seller_stats (user_id)
            values(:userId)
            on conflict (user_id) do nothing
            """, nativeQuery = true)
    void ensureSellerStats(@Param("userId") Long userId);

    @Query(value = """
            select user_id
            from seller_stats
            where user_id = :userId
            for update
            """, nativeQuery = true)
    Long lockSellerStats(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            insert into analytics_products (
                product_id, user_id, price, count,
                count_of_sales, average_estimation
            )
            values (
                :productId, :userId, :price, :count,
                :countOfSales, :averageEstimation
            )
            on conflict (product_id) do update
            set price = excluded.price,
                count = excluded.count,
                count_of_sales = excluded.count_of_sales,
                average_estimation = excluded.average_estimation
            """, nativeQuery = true)
    void upsertProduct(
            @Param("productId") Long productId,
            @Param("userId") Long userId,
            @Param("price") BigDecimal price,
            @Param("count") Integer count,
            @Param("countOfSales") Long countOfSales,
            @Param("averageEstimation") BigDecimal averageEstimation
    );


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = """
            update seller_stats s
            set total_products = totals.total_products,
                total_sales = totals.total_sales,
                total_stock_value = totals.total_stock_value,
                average_estimation = totals.average_estimation,
                updated_at = NOW()
            from (
                select count(*) as total_products, 
                    coalesce(sum(count_of_sales), 0) as total_sales, 
                    coalesce(sum(price * count), 0) as total_stock_value, 
                    coalesce(avg(average_estimation), 0) as average_estimation
                from analytics_products    
                where user_id = :userId
            ) totals
            where s.user_id = :userId
            """, nativeQuery = true)
    void recalculateSellerStats(@Param("userId") Long userId);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        delete from analytics_products
        where product_id = :productId
          and user_id = :userId
        """, nativeQuery = true)
    void deleteProduct(
            @Param("productId") Long productId,
            @Param("userId") Long userId);

}