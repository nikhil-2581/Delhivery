CREATE TABLE food_preferences (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT  NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    is_vegetarian BOOLEAN NOT NULL DEFAULT false,
    allergies     TEXT[],
    cuisines      TEXT[]
);