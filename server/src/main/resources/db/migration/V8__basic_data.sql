-- Asset Location dictionary
CREATE TABLE asset_location (
    id BIGINT PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    parent_id BIGINT,
    address VARCHAR(512),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    sort_order INTEGER DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_asset_loc_parent ON asset_location(parent_id);
CREATE INDEX idx_asset_loc_tenant ON asset_location(tenant_id);

-- Asset Supplier dictionary
CREATE TABLE asset_supplier (
    id BIGINT PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    contact VARCHAR(128),
    phone VARCHAR(32),
    address VARCHAR(512),
    business_scope VARCHAR(512),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_asset_supplier_tenant ON asset_supplier(tenant_id);

-- Asset Maintenance Provider dictionary
CREATE TABLE asset_maintenance_provider (
    id BIGINT PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    contact VARCHAR(128),
    phone VARCHAR(32),
    service_types VARCHAR(512),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_asset_maint_prov_tenant ON asset_maintenance_provider(tenant_id);
