CREATE TABLE asset_change (
    id BIGINT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    from_org_id BIGINT,
    to_org_id BIGINT,
    from_custodian VARCHAR(64),
    to_custodian VARCHAR(64),
    change_value DECIMAL(18,2),
    change_date DATE,
    reason VARCHAR(512),
    remark TEXT,
    operator_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_change_asset ON asset_change(asset_id);
