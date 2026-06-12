CREATE TABLE purchase_request (
    id BIGINT PRIMARY KEY,
    asset_name VARCHAR(256) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    budget DECIMAL(18,2) NOT NULL,
    supplier_id BIGINT,
    org_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    request_reason TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    approval_instance_id VARCHAR(128),
    remark TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_purchase_request_org ON purchase_request(org_id);
CREATE INDEX idx_purchase_request_tenant ON purchase_request(tenant_id);
CREATE INDEX idx_purchase_request_status ON purchase_request(status);

CREATE TABLE purchase_acceptance (
    id BIGINT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    acceptance_result VARCHAR(16) NOT NULL,
    actual_quantity INTEGER,
    actual_amount DECIMAL(18,2),
    check_remark TEXT,
    asset_id BIGINT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_purchase_acceptance_request ON purchase_acceptance(request_id);
