package com.example.authservice.repository;

import com.example.authservice.entities.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select t
        from RefreshToken t
        where t.tokenHash = :tokenHash
        """)
    Optional<RefreshToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Transactional
    @Query("""
        delete from RefreshToken t
        where t.userId = :userId
        """)
    void deleteAllByUserId(@Param("userId") Long userId);
}