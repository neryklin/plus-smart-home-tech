DROP TABLE IF EXISTS warehouse_product CASCADE;

CREATE TABLE IF NOT EXISTS warehouse_product (
    product_id UUID PRIMARY KEY,
    width      double precision NOT NULL,
    height     double precision NOT NULL,
    depth      double precision NOT NULL,
    weight     double precision NOT NULL,
    fragile BOOLEAN NOT NULL,
    quantity BIGINT NOT NULL
);