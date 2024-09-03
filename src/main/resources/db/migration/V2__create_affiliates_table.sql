CREATE TABLE affiliates
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT UNIQUE      NOT NULL,
    affiliate_code TEXT UNIQUE NOT NULL,
    created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
