CREATE TABLE depreciation (
    id BIGINT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    depreciation_amount DECIMAL(18,2) NOT NULL,
    depreciation_date DATE NOT NULL,
    before_value DECIMAL(18,2),
    after_value DECIMAL(18,2),
    period VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_dep_asset ON depreciation(asset_id);
CREATE INDEX idx_dep_period ON depreciation(period);
