package com.example.userservice.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.User;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> {
        User u = new User();
        u.setUserId(rs.getLong("user_id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setAge(rs.getInt("age"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        return u;
    };

    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users;", ROW_MAPPER);
    }

    public Optional<User> getById(Long userId) {
        List<User> list = jdbcTemplate.query(
                "SELECT * FROM users WHERE user_id = ?;", ROW_MAPPER, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public User create(String firstName, String lastName, Integer age, String email,
                       String role, String status) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (first_name, last_name, age, email, role, status) " +
                            "VALUES (?,?,?,?,?,?);",
                    new String[]{"user_id"});
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, age);
            ps.setString(4, email);
            ps.setString(5, role);
            ps.setString(6, status);
            return ps;
        }, kh);
        Long userId = kh.getKey().longValue();
        return getById(userId).orElseThrow();
    }

    public void update(Long userId, String firstName, String lastName, Integer age, String email) {
        jdbcTemplate.update(
                "UPDATE users SET first_name = ?, last_name = ?, age = ?, email = ? WHERE user_id = ?;",
                firstName, lastName, age, email, userId);
    }

    public void updateStatus(Long userId, String status) {
        jdbcTemplate.update("UPDATE users SET status = ? WHERE user_id = ?;", status, userId);
    }

    public void deleteById(Long userId) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?;", userId);
    }
}