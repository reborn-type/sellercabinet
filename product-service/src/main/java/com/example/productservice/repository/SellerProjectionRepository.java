package com.example.productservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.productservice.entities.SellerProjection;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface SellerProjectionRepository extends JpaRepository<SellerProjection, Long> {

    default boolean exists(Long userId){
        return existsById(userId);
    }

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query(value = """
            INSERT INTO sellers_projection (
                user_id, first_name, last_name, email, updated_at
            )
            VALUES (
                :userId,
                :firstName,
                :lastName,
                :email,
                now()
            )
            ON CONFLICT (user_id) DO update
            SET first_name = excluded.first_name,
                last_name = excluded.last_name,
                email = excluded.email,
                updated_at = now()
            """, nativeQuery = true)
    public void upsert(
            @Param("userId") Long userId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM SellerProjection s WHERE s.userId = :userId")
    public void delete(@Param("userId") Long userId);
}