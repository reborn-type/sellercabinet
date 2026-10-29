CREATE TABLE IF NOT EXISTS auth_credentials (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'SELLER',

    CONSTRAINT uk_auth_credentials_email UNIQUE (email),
    CONSTRAINT chk_auth_email_not_blank CHECK (BTRIM(email) <> ''),
    CONSTRAINT chk_auth_password_hash_not_blank CHECK (BTRIM(password_hash) <> ''),
    CONSTRAINT chk_auth_role CHECK(role IN ('SELLER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_refresh_tokens_token_hash
        UNIQUE (token_hash),

    CONSTRAINT chk_refresh_token_hash_not_blank
        CHECK (BTRIM(token_hash) <> ''),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES auth_credentials(user_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user
    ON refresh_tokens(user_id);
