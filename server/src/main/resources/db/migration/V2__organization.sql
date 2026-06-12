CREATE TABLE sys_organization (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(128) NOT NULL,
    org_type VARCHAR(32) NOT NULL,
    org_code VARCHAR(64),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    sort_order INTEGER DEFAULT 0,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_sys_org_parent ON sys_organization(parent_id);
CREATE INDEX idx_sys_org_tenant ON sys_organization(tenant_id);
CREATE INDEX idx_sys_org_type ON sys_organization(org_type);
