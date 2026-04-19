CREATE TABLE addresses (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label      VARCHAR(50)  NOT NULL,
    line1      VARCHAR(200) NOT NULL,
    city       VARCHAR(100) NOT NULL,
    pincode    VARCHAR(10)  NOT NULL,
    is_default BOOLEAN      NOT NULL DEFAULT false
);