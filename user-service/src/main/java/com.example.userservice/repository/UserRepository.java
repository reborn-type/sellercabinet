package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;

import com.example.userservice.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT NEW com.example.userservice.dto.UserResponse (
                u.userId, u.firstName, u.lastName, u.age, u.email, u.role, u.status
            )
            FROM User u
            ORDER BY u.userId
            """)
    public List<UserResponse> getALl();

    @Query("""
            SELECT NEW com.example.userservice.dto.UserResponse(
                u.userId, u.firstName, u.lastName, u.age, u.email, u.role, u.status
            )
            FROM User u
            WHERE u.userId = :userId
           """)
    public Optional<UserResponse> findResponseById(@Param("userId") Long userId);
}