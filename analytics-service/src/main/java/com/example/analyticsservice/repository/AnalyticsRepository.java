package com.example.analyticsservice.repository;

import com.example.analyticsservice.entities.SellerStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return s;
    };

    public void incrementProducts(Long sellerId) {
        jdbc.update("""
            INSERT INTO seller_stats (seller_id, total_products) VALUES (?, 1)
            ON CONFLICT (seller_id) DO UPDATE SET 
                total_products = seller_stats.total_products + 1,
                updated_at = NOW();
            """, sellerId);
    }

    public void decrementProducts(Long sellerId) {
        jdbc.update("""
            INSERT INTO seller_stats (seller_id, total_products) VALUES (?, 0)
            ON CONFLICT (seller_id) DO UPDATE SET 
                total_products = GREATEST(0, seller_stats.total_products - 1),
                updated_at = NOW();
            """, sellerId);
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