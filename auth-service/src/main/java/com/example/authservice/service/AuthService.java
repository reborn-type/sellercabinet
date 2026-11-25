package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.dto.TokenPair;
import com.example.authservice.entities.Credential;
import com.example.authservice.entities.RefreshToken;
import com.example.authservice.exception.EmailAlreadyExistsException;
import com.example.authservice.exception.InvalidCredentialsException;
import com.example.authservice.exception.InvalidRefreshTokenException;
import com.example.authservice.kafka.AuthEventPublisher;
import com.example.authservice.model.UserRole;
import com.example.authservice.repository.CredentialRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CredentialRepository credentials;
    private final RefreshTokenRepository refreshTokens;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventPublisher eventPublisher;
    private final RefreshTokenHasher refreshTokenHasher;

    public AuthService(
            CredentialRepository credentials,
            RefreshTokenRepository refreshTokens,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            AuthEventPublisher eventPublisher,
            RefreshTokenHasher refreshTokenHasher) {
        this.credentials = credentials;
        this.refreshTokens = refreshTokens;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.refreshTokenHasher = refreshTokenHasher;
    }

    @Transactional
    public TokenPair register(RegisterRequest req) {
        if (credentials.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException();
        }

        String role = UserRole.SELLER.name();
        Credential created;

        try {
            created = credentials.saveAndFlush(new Credential(
                    req.email(),
                    passwordEncoder.encode(req.password()),
                    role
            ));
        } catch (DataIntegrityViolationException exception) {
            if (exception.getCause()
                    instanceof ConstraintViolationException violation
                    && ("uk_auth_credentials_email"
                    .equals(violation.getConstraintName())
                    || "auth_credentials_email_key"
                    .equals(violation.getConstraintName()))) {
                throw new EmailAlreadyExistsException();
            }

            throw exception;
        }

        TokenPair tokens = issueTokens(created);
        Long userId = created.getUserId();

        afterCommit(() -> eventPublisher.publishRegistrationRequested(
                userId,
                req.firstName(),
                req.lastName(),
                req.age(),
                req.email(),
                role
        ));

        return tokens;
    }

    @Transactional
    public TokenPair login(LoginRequest req) {
        Credential credential = credentials.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                req.password(),
                credential.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueTokens(credential);
    }

    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public TokenPair refresh(String oldRawRefreshToken) {
        String oldTokenHash = refreshTokenHasher.hash(
                oldRawRefreshToken);

        RefreshToken stored = refreshTokens
                .findByTokenHashForUpdate(oldTokenHash)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Недействительный refresh токен"));

        refreshTokens.delete(stored);
        refreshTokens.flush();

        if (!stored.getExpiresAt().isAfter(Instant.now())) {
            throw new InvalidRefreshTokenException(
                    "Refresh токен истек");
        }

        Credential credential = credentials
                .findById(stored.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "Пользователь не найден"));

        return issueTokens(credential);
    }

    private TokenPair issueTokens(Credential credential) {
        String accessToken = tokenService.generateAccessToken(
                credential.getUserId(),
                credential.getRole()
        );

        String refreshToken = createRefreshToken(
                credential.getUserId());

        return new TokenPair(accessToken, refreshToken);
    }

    private String createRefreshToken(Long userId) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        refreshTokens.save(new RefreshToken(
                userId,
                refreshTokenHasher.hash(rawToken),
                Instant.now().plus(7, ChronoUnit.DAYS)
        ));

        return rawToken;
    }

    private void afterCommit(Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        action.run();
                    }
                }
        );
    }
}