package com.example.authservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class CredentialRepository {
    private final JdbcTemplate jdbc;

    public CredentialRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Credential(Long userId, String email, String passwordHash, String role) {}

    private static final org.springframework.jdbc.core.RowMapper<Credential> ROW_MAPPER =
            (rs, i) -> new Credential(
                    rs.getLong("user_id"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("role")
            );

    public boolean existsByEmail(String email) {
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM auth_credentials WHERE email = ?)",
                Boolean.class, email);
        return Boolean.TRUE.equals(exists);
    }

    public Long save(String email, String passwordHash, String role) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO auth_credentials (email, password_hash, role) VALUES (?, ?, ?)",
                    new String[]{"user_id"});
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public Optional<Credential> findByEmail(String email) {
        return jdbc.query(
                "SELECT user_id, email, password_hash, role FROM auth_credentials WHERE email = ?",
                ROW_MAPPER, email).stream().findFirst();
    }

    public Optional<Credential> findByUserId(Long userId) {
        return jdbc.query(
                "SELECT user_id, email, password_hash, role FROM auth_credentials WHERE user_id = ?",
                ROW_MAPPER, userId).stream().findFirst();
    }
}