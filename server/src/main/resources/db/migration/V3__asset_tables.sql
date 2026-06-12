CREATE TABLE asset_category (
    id BIGINT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    parent_id BIGINT,
    level INTEGER DEFAULT 1,
    depreciation_method VARCHAR(32) DEFAULT 'STRAIGHT_LINE',
    default_useful_life INTEGER,
    default_residual_rate DECIMAL(5,4) DEFAULT 0.05,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

INSERT INTO asset_category (id, code, name, parent_id, level) VALUES
(1, 'LAND', '土地', NULL, 1),
(2, 'REAL_ESTATE', '房屋及建筑物', NULL, 1),
(3, 'EQUIPMENT', '机器设备', NULL, 1),
(4, 'VEHICLE', '车辆', NULL, 1),
(5, 'FURNITURE', '办公家具', NULL, 1),
(6, 'IT_EQUIPMENT', '电子设备', NULL, 1);

CREATE TABLE asset (
    id BIGINT PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    asset_code VARCHAR(64) NOT NULL UNIQUE,
    category VARCHAR(32) NOT NULL,
    org_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    specification VARCHAR(256),
    unit VARCHAR(16),
    quantity INTEGER DEFAULT 1,
    original_value DECIMAL(18,2),
    current_value DECIMAL(18,2),
    accumulated_depreciation DECIMAL(18,2) DEFAULT 0,
    depreciation_method VARCHAR(32),
    useful_life_months INTEGER,
    residual_rate DECIMAL(5,4) DEFAULT 0.05,
    use_status VARCHAR(32) DEFAULT 'IN_USE',
    use_department VARCHAR(128),
    custodian VARCHAR(64),
    location VARCHAR(256),
    address VARCHAR(512),
    purchase_date DATE,
    registration_date DATE,
    source_type VARCHAR(32),
    certificate_no VARCHAR(64),
    photo_ids TEXT,
    remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_asset_org ON asset(org_id);
CREATE INDEX idx_asset_tenant ON asset(tenant_id);
CREATE INDEX idx_asset_category ON asset(category);
CREATE INDEX idx_asset_code ON asset(asset_code);
CREATE INDEX idx_asset_status ON asset(use_status);
