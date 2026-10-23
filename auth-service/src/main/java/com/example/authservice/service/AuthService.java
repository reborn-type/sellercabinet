package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.TokenPair;
import com.example.authservice.kafka.AuthEventPublisher;
import com.example.authservice.repository.CredentialRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final CredentialRepository credentials;
    private final RefreshTokenRepository refreshTokens;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventPublisher eventPublisher;

    public AuthService(CredentialRepository credentials,
                       RefreshTokenRepository refreshTokens,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       AuthEventPublisher eventPublisher) {
        this.credentials = credentials;
        this.refreshTokens = refreshTokens;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TokenPair register(RegisterRequest req) {
        if (credentials.existsByEmail(req.email())) {
            throw new IllegalStateException("Email уже занят");
        }

        Long userId = credentials.save(
                req.email(),
                passwordEncoder.encode(req.password()),
                req.role()
        );

        eventPublisher.publishRegistrationRequested(
                userId, req.firstName(), req.lastName(), req.age(), req.email(), req.role()
        );

        String access = tokenService.generateAccessToken(userId, req.role());
        String refresh = createRefreshToken(userId);
        return new TokenPair(access, refresh);
    }

    @Transactional
    public TokenPair login(LoginRequest req) {
        var cred = credentials.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));

        if (!passwordEncoder.matches(req.password(), cred.passwordHash())) {
            throw new IllegalArgumentException("Неверный email или пароль");
        }

        String access = tokenService.generateAccessToken(cred.userId(), cred.role());
        String refresh = createRefreshToken(cred.userId());
        return new TokenPair(access, refresh);
    }

    @Transactional
    public TokenPair refresh(String oldRefreshToken) {
        var stored = refreshTokens.findByToken(oldRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Недействительный refresh token"));

        if (stored.expiresAt().isBefore(Instant.now())) {
            refreshTokens.deleteByToken(oldRefreshToken);
            throw new IllegalArgumentException("Refresh token истек");
        }

        refreshTokens.deleteByToken(oldRefreshToken);

        var cred = credentials.findByUserId(stored.userId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        String newAccess = tokenService.generateAccessToken(cred.userId(), cred.role());
        String newRefresh = createRefreshToken(cred.userId());
        return new TokenPair(newAccess, newRefresh);
    }

    private String createRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        refreshTokens.save(userId, token, Instant.now().plus(7, ChronoUnit.DAYS));
        return token;
    }
}