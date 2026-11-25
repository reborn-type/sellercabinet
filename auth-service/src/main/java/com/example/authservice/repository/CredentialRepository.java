package com.example.authservice.repository;

import com.example.authservice.entities.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository
        extends JpaRepository<Credential, Long> {

    boolean existsByEmail(String email);

    Optional<Credential> findByEmail(String email);
}