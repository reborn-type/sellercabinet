CREATE TABLE IF NOT EXISTS sellers (
    seller_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    age int NOT NULL, 
    email VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    product_id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(seller_id),
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL, 
    count int, 
    count_of_sales int, 
    average_estimation NUMERIC(10,2) NOT NULL
);

ALTER TABLE products
DROP CONSTRAINT IF EXISTS products_seller_id_fkey;

ALTER TABLE products
ADD CONSTRAINT products_seller_id_fkey
FOREIGN KEY (seller_id)
REFERENCES sellers(seller_id)
ON DELETE CASCADE;