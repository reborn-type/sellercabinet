package com.example.sellercabinet.repository;

import java.sql.PreparedStatement;
import java.util.List; 
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder; 
import org.springframework.stereotype.Repository;

import com.example.sellercabinet.entities.Product;


@Repository 
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate; 

    public ProductRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate; 
    }

    private static final RowMapper<Product> ROW_MAPPER = (rs, rowNum) -> {
        Product p = new Product(); 
        p.setProductId(rs.getLong("product_id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setCount(rs.getInt("count"));
        p.setCountOfSales(rs.getInt("count_of_sales"));
        p.setAverageEstimation(rs.getDouble("average_estimation"));
        return p; 
    };

    public List<Product> getAll() {
        return jdbcTemplate.query("SELECT * FROM products;", ROW_MAPPER);
    }

    public Long getTotalSales() {
        String sql = "SELECT COALESCE(SUM(count_of_sales), 0) FROM products";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }



    public Optional<Product> getById(Long productId){
        List<Product> list = jdbcTemplate.query(
            "SELECT * FROM products WHERE product_id = ?;", ROW_MAPPER, productId
        );
        if (list.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    };

    public List<Product> findBySellerId(Long sellerId){
        return jdbcTemplate.query(
            "SELECT * FROM products WHERE seller_id = ?", ROW_MAPPER, sellerId
        );
    }

    public Product findByProductAndSellerId(Long sellerId, Long productId){
        return jdbcTemplate.queryForObject(
            "SELECT * FROM products WHERE seller_id = ? AND product_id = ?;",
            ROW_MAPPER,
            sellerId,
            productId
        );
    }

    public Product create(Long sellerId, String name, double price, int count, int countOfSales, double averageEstimation){
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO products (seller_id, name, price, count, count_of_sales, average_estimation) VALUES (?,?,?,?,?,?);",
                new String[]{"product_id"}
            );
            ps.setLong(1, sellerId);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, count);
            ps.setInt(5, countOfSales);
            ps.setDouble(6, averageEstimation);
            return ps; 
        }, kh);
        Long productId = kh.getKey().longValue();
        return getById(productId).orElseThrow();
    }

    public void update(Long productId, Long sellerId, String name, Double price, int count, int countOfSales, double average_estimation){
        jdbcTemplate.update(
            "UPDATE products SET name = ?,price=?,count=?,count_of_sales=?,average_estimation=? WHERE product_id = ? AND seller_id = ?;",
            name, price, count, countOfSales, average_estimation, productId, sellerId
        );
    }

    public void deleteById(Long sellerId, Long productId){
        jdbcTemplate.update(
            "DELETE FROM products WHERE seller_id = ? and product_id = ?;",
            sellerId, productId
        );
    }

    public void deleteByProductId(Long productId){
        jdbcTemplate.update(
                "DELETE FROM products WHERE product_id = ?;",
                productId
        );
    }


}
