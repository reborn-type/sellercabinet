CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    count INT NOT NULL,
    count_of_sales INT NOT NULL DEFAULT 0,
    average_estimation DOUBLE PRECISION NOT NULL DEFAULT 0,
);

CREATE INDEX idx_products_seller ON products(seller_id);

CREATE TABLE sellers_projection (
    seller_id BIGINT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);