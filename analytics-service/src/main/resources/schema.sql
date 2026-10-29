CREATE TABLE IF NOT EXISTS analytics_products (
    product_id BIGINT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    count INT NOT NULL,
    count_of_sales INT NOT NULL,
    average_estimation DECIMAL(3, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS seller_stats (
    seller_id BIGINT PRIMARY KEY,
    total_products INT NOT NULL DEFAULT 0,
    total_sales BIGINT NOT NULL DEFAULT 0,
    total_stock_value DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    average_estimation DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);