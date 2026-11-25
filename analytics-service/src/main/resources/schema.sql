-- Rename the legacy column in databases created before user_id was introduced.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'analytics_products'
          AND column_name = 'seller_id'
    ) THEN
        ALTER TABLE analytics_products RENAME COLUMN seller_id TO user_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'seller_stats'
          AND column_name = 'seller_id'
    ) THEN
        ALTER TABLE seller_stats RENAME COLUMN seller_id TO user_id;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS analytics_products (
    product_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    count INT NOT NULL,
    count_of_sales INT NOT NULL,
    average_estimation DECIMAL(3, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS seller_stats (
    user_id BIGINT PRIMARY KEY,
    total_products INT NOT NULL DEFAULT 0,
    total_sales BIGINT NOT NULL DEFAULT 0,
    total_stock_value DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    average_estimation DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);