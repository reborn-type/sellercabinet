package com.example.analyticsservice.repository;

import com.example.analyticsservice.entities.SellerStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AnalyticsRepository {
    private final JdbcTemplate jdbc;

    public AnalyticsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<SellerStats> ROW_MAPPER = (rs, i) -> {
        SellerStats s = new SellerStats();
        s.setSellerId(rs.getLong("seller_id"));
        s.setTotalProducts(rs.getInt("total_products"));
        s.setTotalSales(rs.getLong("total_sales"));
        s.setTotalStockValue(rs.getBigDecimal("total_stock_value"));
        s.setAverageEstimation(rs.getBigDecimal("average_estimation"));
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return s;
    };

    public void upsertProduct(Long productId, Long sellerId, BigDecimal price,
                              Integer count, Integer countOfSales, BigDecimal averageEstimation) {
        jdbc.update("""
            INSERT INTO analytics_products (product_id, seller_id, price, count, count_of_sales, average_estimation)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (product_id) DO UPDATE SET
                price = EXCLUDED.price,
                count = EXCLUDED.count,
                count_of_sales = EXCLUDED.count_of_sales,
                average_estimation = EXCLUDED.average_estimation;
            """, productId, sellerId, price, count, countOfSales, averageEstimation);

        recalculateSellerStats(sellerId);
    }

    public void deleteProduct(Long productId, Long sellerId) {
        jdbc.update("DELETE FROM analytics_products WHERE product_id = ?", productId);
        recalculateSellerStats(sellerId);
    }

    private void recalculateSellerStats(Long sellerId) {
        jdbc.update("""
            INSERT INTO seller_stats (seller_id, total_products, total_sales, total_stock_value, average_estimation, updated_at)
            SELECT 
                ?,
                COUNT(*),
                COALESCE(SUM(count_of_sales), 0),
                COALESCE(SUM(price * count), 0),
                COALESCE(AVG(average_estimation), 0),
                NOW()
            FROM analytics_products
            WHERE seller_id = ?
            ON CONFLICT (seller_id) DO UPDATE SET
                total_products = EXCLUDED.total_products,
                total_sales = EXCLUDED.total_sales,
                total_stock_value = EXCLUDED.total_stock_value,
                average_estimation = EXCLUDED.average_estimation,
                updated_at = NOW();
            """, sellerId, sellerId);
    }

    public List<SellerStats> getAllStats() {
        return jdbc.query("SELECT * FROM seller_stats ORDER BY total_sales DESC", ROW_MAPPER);
    }

    public SellerStats getStatsBySellerId(Long sellerId) {
        List<SellerStats> list = jdbc.query(
                "SELECT * FROM seller_stats WHERE seller_id = ?", ROW_MAPPER, sellerId);
        return list.isEmpty() ? null : list.get(0);
    }
}