CREATE TABLE referrals
(
    id               BIGSERIAL PRIMARY KEY,
    affiliate_id     BIGINT REFERENCES affiliates (id),
    referred_user_id BIGINT UNIQUE NOT NULL,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (referred_user_id) REFERENCES users (id)
);

CREATE INDEX idx_referrals_affiliate_id ON referrals (affiliate_id);
