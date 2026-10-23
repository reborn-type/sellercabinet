CREATE TABLE IF NOT EXISTS seller_stats (
    seller_id BIGINT PRIMARY KEY,
    total_products INT NOT NULL DEFAULT 0,
    total_sales BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);