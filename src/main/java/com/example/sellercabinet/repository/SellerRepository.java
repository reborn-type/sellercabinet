package com.example.sellercabinet.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.sellercabinet.entities.Seller;

@Repository 
public class SellerRepository {
    private final JdbcTemplate jdbcTemplate; 

    public SellerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Seller> ROW_MAPPER = (rs, rowNum) -> {
        Seller s = new Seller();
        s.setSellerId(rs.getLong("seller_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setAge(rs.getInt("age"));
        s.setEmail(rs.getString("email"));
        return s; 
    };

    public List<Seller> getAll() {
        return jdbcTemplate.query("SELECT * FROM sellers;", ROW_MAPPER);
    }

    public Optional<Seller> getById(Long seller_id){
        List <Seller> list = jdbcTemplate.query(
            "SELECT * FROM sellers WHERE seller_id = ?;", ROW_MAPPER, seller_id
        );
        if (list.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    };

    public Seller create(String first_name, String last_name, int age, String email){
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO sellers (first_name, last_name, age, email) VALUES (?,?,?,?);",
                new String[]{"seller_id"}
            );
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setInt(3, age);
            ps.setString(4,email);
            return ps; 
        }, kh);
        Long seller_id = kh.getKey().longValue();
        return getById(seller_id).orElseThrow();
    }

    public void update(Long seller_id, String first_name, String last_name, int age, String email){
        jdbcTemplate.update(
            "UPDATE sellers SET first_name = ?, last_name = ?, age = ?, email = ? WHERE seller_id = ?;",
            first_name, last_name, age, email, seller_id
        );
    }

    public void deleteById (Long seller_id){
        jdbcTemplate.update(
            "DELETE FROM sellers WHERE seller_id = ?;",
            seller_id
        );
    }
    
}
