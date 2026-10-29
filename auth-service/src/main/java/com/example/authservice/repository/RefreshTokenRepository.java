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

    public record StoredRefresh(
            Long userId,
            Instant expiresAt
    ) {}

    public void save(
            Long userId,
            String tokenHash,
            Instant expiresAt
    ) {
        jdbc.update(
                """
                INSERT INTO refresh_tokens (
                    user_id,
                    token_hash,
                    expires_at
                )
                VALUES (?, ?, ?)
                """,
                userId,
                tokenHash,
                java.sql.Timestamp.from(expiresAt)
        );
    }

    public Optional<StoredRefresh> consumeByHash(String tokenHash) {
        return jdbc.query(
                """
                DELETE FROM refresh_tokens
                WHERE token_hash = ?
                RETURNING user_id, expires_at
                """,
                (resultSet, rowNumber) -> new StoredRefresh(
                        resultSet.getLong("user_id"),
                        resultSet.getTimestamp("expires_at").toInstant()
                ),
                tokenHash
        ).stream().findFirst();
    }

    public void deleteAllByUserId(Long userId) {
        jdbc.update(
                "DELETE FROM refresh_tokens WHERE user_id = ?",
                userId
        );
    }
}