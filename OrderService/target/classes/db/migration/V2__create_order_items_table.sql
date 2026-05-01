CREATE TABLE order_items (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    name       VARCHAR(150)    NOT NULL,
    quantity   INT             NOT NULL,
    unit_price NUMERIC(10,2)   NOT NULL
);