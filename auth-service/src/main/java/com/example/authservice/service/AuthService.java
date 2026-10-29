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

import com.example.authservice.model.UserRole;
import com.example.authservice.exception.*;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AuthService {

    private final CredentialRepository credentials;
    private final RefreshTokenRepository refreshTokens;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventPublisher eventPublisher;
    private final RefreshTokenHasher refreshTokenHasher;


    public AuthService(CredentialRepository credentials,
                       RefreshTokenRepository refreshTokens,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       AuthEventPublisher eventPublisher,
                       RefreshTokenHasher refreshTokenHasher
    ) {
        this.credentials = credentials;
        this.refreshTokens = refreshTokens;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private String createRefreshToken(Long userId) {
        String rawToken = generateRawRefreshToken();
        String tokenHash = refreshTokenHasher.hash(rawToken);

        refreshTokens.save(
                userId,
                tokenHash,
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return rawToken;
    }

    private String generateRawRefreshToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Transactional(
            noRollbackFor = InvalidRefreshTokenException.class
    )
    public TokenPair refresh(String oldRawRefreshToken) {
        String oldTokenHash =
                refreshTokenHasher.hash(oldRawRefreshToken);

        var stored = refreshTokens.consumeByHash(oldTokenHash)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Недействительный refresh токен"
                        )
                );

        if (stored.expiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException(
                    "Refresh токен истек"
            );
        }

        var credential = credentials.findByUserId(stored.userId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Пользователь не найден"
                        )
                );

        String newAccessToken =
                tokenService.generateAccessToken(
                        credential.userId(),
                        credential.role()
                );

        String newRefreshToken =
                createRefreshToken(credential.userId());

        return new TokenPair(
                newAccessToken,
                newRefreshToken
        );
    }




    @Transactional
    public TokenPair register(RegisterRequest req) {
        if (credentials.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException();
        }

        String role = UserRole.SELLER.name();

        Long userId = credentials.save(
                req.email(),
                passwordEncoder.encode(req.password()),
                role
        );

        eventPublisher.publishRegistrationRequested(
                userId, req.firstName(), req.lastName(), req.age(), req.email(), role
        );

        String access = tokenService.generateAccessToken(userId, role);
        String refresh = createRefreshToken(userId);
        return new TokenPair(access, refresh);
    }

    @Transactional
    public TokenPair login(LoginRequest req) {
        var cred = credentials.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(req.password(), cred.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String access = tokenService.generateAccessToken(cred.userId(), cred.role());
        String refresh = createRefreshToken(cred.userId());
        return new TokenPair(access, refresh);
    }

}