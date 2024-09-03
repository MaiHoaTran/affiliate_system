CREATE TABLE commissions
(
    id          BIGSERIAL PRIMARY KEY,
    referral_id BIGINT UNIQUE    NOT NULL,
    amount      DOUBLE PRECISION NOT NULL,
    status      VARCHAR(10)      NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (referral_id) REFERENCES referrals (id)
);

CREATE INDEX idx_commissions_status_created_at ON commissions (status, created_at);
