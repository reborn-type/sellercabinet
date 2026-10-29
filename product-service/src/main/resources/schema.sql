CREATE TABLE IF NOT EXISTS products (
    product_id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    price NUMERIC(14,2) NOT NULL,
    count INT NOT NULL,
    count_of_sales INT NOT NULL DEFAULT 0,
    average_estimation NUmERIC(3,2) NOT NULL,

    CONSTRAINT chk_product_name CHECK (BTRIM(name) <> ''),
    CONSTRAINT chk_product_price CHECK (price > 0),
    CONSTRAINT chk_product_count CHECK (count >= 0),
    CONSTRAINT chk_product_sales CHECK (count_of_sales >= 0),
    CONSTRAINT chk_product_estimation CHECK (average_estimation BETWEEN 1.00 and 5.00)
);

CREATE INDEX IF NOT EXISTS idx_products_seller ON products(seller_id);

CREATE TABLE IF NOT EXISTS sellers_projection (
    seller_id BIGINT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(200),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
