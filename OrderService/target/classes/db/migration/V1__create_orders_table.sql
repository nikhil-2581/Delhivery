CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT          NOT NULL,
    status           VARCHAR(30)     NOT NULL,
    delivery_address VARCHAR(300)    NOT NULL,
    subtotal         NUMERIC(10,2)   NOT NULL,
    delivery_fee     NUMERIC(10,2)   NOT NULL,
    discount         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    total            NUMERIC(10,2)   NOT NULL,
    placed_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW()
);