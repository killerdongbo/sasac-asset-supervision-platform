CREATE TABLE approval_def (
    id BIGINT PRIMARY KEY,
    def_name VARCHAR(256) NOT NULL,
    biz_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    description TEXT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_approval_def_tenant ON approval_def(tenant_id);
CREATE INDEX idx_approval_def_biz_type ON approval_def(biz_type);

CREATE TABLE approval_node (
    id BIGINT PRIMARY KEY,
    def_id BIGINT NOT NULL,
    node_order INTEGER NOT NULL,
    approver_role VARCHAR(128) NOT NULL,
    condition_expr VARCHAR(512),
    can_reject BOOLEAN NOT NULL DEFAULT TRUE,
    timeout_hours INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_approval_node_def ON approval_node(def_id);
CREATE INDEX idx_approval_node_def_order ON approval_node(def_id, node_order);

CREATE TABLE approval_instance (
    id BIGINT PRIMARY KEY,
    def_id BIGINT NOT NULL,
    biz_id BIGINT NOT NULL,
    biz_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    current_node INTEGER NOT NULL DEFAULT 1,
    node_results TEXT,
    submitter_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_approval_instance_def ON approval_instance(def_id);
CREATE INDEX idx_approval_instance_tenant ON approval_instance(tenant_id);
CREATE INDEX idx_approval_instance_status ON approval_instance(status);
CREATE INDEX idx_approval_instance_submitter ON approval_instance(submitter_id);
CREATE INDEX idx_approval_instance_biz ON approval_instance(biz_type, biz_id);
