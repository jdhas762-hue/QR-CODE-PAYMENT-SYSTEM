-- PostgreSQL/MySQL compatible core schema for secure dynamic QR payment system

CREATE TABLE merchant_sessions (
    session_id VARCHAR(64) PRIMARY KEY,
    merchant_id VARCHAR(64) NOT NULL UNIQUE,
    device_fingerprint VARCHAR(255) NOT NULL,
    ip_address VARCHAR(64) NOT NULL,
    location VARCHAR(128) NOT NULL,
    login_timestamp TIMESTAMP NOT NULL,
    qr_regeneration_count INT DEFAULT 0,
    location_change_count INT DEFAULT 0,
    repeated_device_change_count INT DEFAULT 0,
    fraud_probability_score DECIMAL(5,2) DEFAULT 0,
    risk_level VARCHAR(16) DEFAULT 'LOW_RISK'
);

CREATE TABLE qr_transactions (
    transaction_id VARCHAR(64) PRIMARY KEY,
    merchant_id VARCHAR(64) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    created_timestamp TIMESTAMP NOT NULL,
    device_fingerprint VARCHAR(255),
    ip_address VARCHAR(64) NOT NULL,
    location VARCHAR(128) NOT NULL,
    nonce VARCHAR(128) NOT NULL UNIQUE,
    hmac_signature TEXT NOT NULL,
    qr_status VARCHAR(16) DEFAULT 'CREATED',
    expired_attempts INT DEFAULT 0,
    device_mismatch_attempts INT DEFAULT 0,
    amount_mismatch_attempts INT DEFAULT 0,
    replay_attempts INT DEFAULT 0,
    CONSTRAINT fk_qr_merchant FOREIGN KEY (merchant_id)
      REFERENCES merchant_sessions (merchant_id)
);

CREATE INDEX idx_qr_txn_merchant_time ON qr_transactions (merchant_id, created_timestamp);
CREATE INDEX idx_qr_txn_status ON qr_transactions (qr_status);

CREATE TABLE qr_security_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(64) NOT NULL,
    merchant_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    event_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    metadata_json TEXT,
    CONSTRAINT fk_event_txn FOREIGN KEY (transaction_id)
      REFERENCES qr_transactions (transaction_id)
);

-- Aggregation query for module 4 dashboard
-- Merchant risk snapshot with suspicious/replay/expired counters
SELECT
    t.merchant_id,
    AVG(ms.fraud_probability_score) AS risk_score,
    AVG(ms.fraud_probability_score) AS fraud_probability,
    SUM(t.amount_mismatch_attempts + t.device_mismatch_attempts) AS suspicious_attempt_count,
    SUM(t.replay_attempts) AS replay_attempt_count,
    SUM(t.expired_attempts) AS expired_qr_misuse_attempts,
    SUM(t.device_mismatch_attempts) AS device_mismatch_count,
    MAX(ms.location_change_count) AS location_change_count
FROM qr_transactions t
JOIN merchant_sessions ms ON ms.merchant_id = t.merchant_id
GROUP BY t.merchant_id;

-- QR frequency graph input (hourly)
SELECT
    merchant_id,
    EXTRACT(HOUR FROM created_timestamp) AS hour_bucket,
    COUNT(*) AS qr_generated
FROM qr_transactions
GROUP BY merchant_id, EXTRACT(HOUR FROM created_timestamp)
ORDER BY merchant_id, hour_bucket;

-- Risk score trend input
SELECT
    merchant_id,
    login_timestamp,
    fraud_probability_score
FROM merchant_sessions
ORDER BY merchant_id, login_timestamp;
