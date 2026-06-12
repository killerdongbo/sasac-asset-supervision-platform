CREATE TABLE disposal (
    id BIGINT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    disposal_type VARCHAR(32) NOT NULL,
    disposal_date DATE,
    book_value DECIMAL(18,2),
    disposal_value DECIMAL(18,2),
    gain_loss DECIMAL(18,2),
    reason VARCHAR(512),
    approval_id VARCHAR(64),
    remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_disposal_asset ON disposal(asset_id);
