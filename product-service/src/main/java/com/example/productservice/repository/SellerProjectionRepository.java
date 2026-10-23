package com.example.productservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SellerProjectionRepository {
    private final JdbcTemplate jdbc;

    public SellerProjectionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void upsert(Long sellerId, String firstName, String lastName, String email) {
        jdbc.update("""
            INSERT INTO sellers_projection (seller_id, first_name, last_name, email, updated_at)
            VALUES (?,?,?,?, NOW())
            ON CONFLICT (seller_id) DO UPDATE
            SET first_name=EXCLUDED.first_name,
                last_name=EXCLUDED.last_name,
                email=EXCLUDED.email,
                updated_at=NOW();
            """, sellerId, firstName, lastName, email);
    }

    public void markBlocked(Long sellerId) {
        jdbc.update("UPDATE sellers_projection SET status='BLOCKED' WHERE seller_id=?", sellerId);
    }

    public void delete(Long sellerId) {
        jdbc.update("DELETE FROM sellers_projection WHERE seller_id=?", sellerId);
    }

    public boolean exists(Long sellerId) {
        Boolean b = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM sellers_projection WHERE seller_id=?);",
                Boolean.class, sellerId);
        return Boolean.TRUE.equals(b);
    }
}