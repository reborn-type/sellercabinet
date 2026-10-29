CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    role VARCHAR(32) NOT NULL DEFAULT 'SELLER',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE'

    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT chk_user_first_name CHECK(BTRIM(first_name) <> ''),
    CONSTRAINT chk_user_last_name CHECK(BTRIM(last_name) <> ''),
    CONSTRAINT chk_user_age CHECK(age BETWEEN 18 and 110),
    CONSTRAINT chk_user_role CHECK(role in ('SELLER', 'ADMIN')),
    CONSTRAINT chk_user_status CHECK(status in ('ACTIVE', 'BLOCKED'))
);