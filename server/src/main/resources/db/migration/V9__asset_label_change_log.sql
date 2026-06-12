CREATE TABLE asset_label (
    id BIGINT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    label_code VARCHAR(64) NOT NULL,
    barcode_data TEXT,
    print_count INTEGER DEFAULT 0,
    last_print_time TIMESTAMP,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_label_asset ON asset_label(asset_id);
CREATE INDEX idx_label_code ON asset_label(label_code);
CREATE INDEX idx_label_tenant ON asset_label(tenant_id);

CREATE TABLE asset_change_log (
    id BIGINT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    change_field VARCHAR(64) NOT NULL,
    before_value TEXT,
    after_value TEXT,
    operator_id VARCHAR(64),
    remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_change_log_asset ON asset_change_log(asset_id);
CREATE INDEX idx_change_log_field ON asset_change_log(change_field);
