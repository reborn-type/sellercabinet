package com.example.userservice.repository;

import java.util.List;
import java.util.Optional;

import com.example.userservice.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.userservice.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO users (
            user_id,
            first_name,
            last_name,
            age,
            email,
            role,
            status
        )
        VALUES (
            :userId,
            :firstName,
            :lastName,
            :age,
            :email,
            :role,
            'ACTIVE'
        )
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    int insertFromRegistration(
            @Param("userId") Long userId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("age") Integer age,
            @Param("email") String email,
            @Param("role") String role
    );

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