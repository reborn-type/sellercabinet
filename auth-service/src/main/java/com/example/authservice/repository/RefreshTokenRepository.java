package com.example.authservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {
    private final JdbcTemplate jdbc;

    public RefreshTokenRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record StoredRefresh(Long userId, Instant expiresAt) {}

    public void save(Long userId, String token, Instant expiresAt) {
        jdbc.update(
                "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
                userId, token, java.sql.Timestamp.from(expiresAt));
    }

    public Optional<StoredRefresh> findByToken(String token) {
        return jdbc.query(
                "SELECT user_id, expires_at FROM refresh_tokens WHERE token = ?",
                (rs, i) -> new StoredRefresh(
                        rs.getLong("user_id"),
                        rs.getTimestamp("expires_at").toInstant()
                ), token).stream().findFirst();
    }

    public void deleteByToken(String token) {
        jdbc.update("DELETE FROM refresh_tokens WHERE token = ?", token);
    }

    public void deleteAllByUserId(Long userId) {
        jdbc.update("DELETE FROM refresh_tokens WHERE user_id = ?", userId);
    }
}