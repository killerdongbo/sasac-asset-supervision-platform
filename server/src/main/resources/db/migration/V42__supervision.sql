-- ============================================================
-- M9: 监督追责系统 (Supervision & Accountability)
-- Tables: sup_audit_plan, sup_audit_finding, sup_rectification, sup_violation_case
-- ============================================================

CREATE TABLE sup_audit_plan (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    plan_year INTEGER NOT NULL,
    plan_name VARCHAR(256),
    audit_scope TEXT,
    audit_team TEXT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_sup_plan_tenant_year ON sup_audit_plan(tenant_id, plan_year);
CREATE INDEX idx_sup_plan_org ON sup_audit_plan(org_id);

CREATE TABLE sup_audit_finding (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    plan_id BIGINT,
    finding_no VARCHAR(64),
    title VARCHAR(256),
    severity VARCHAR(16),
    description TEXT,
    evidence_ids VARCHAR(512),
    status VARCHAR(32) DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_sup_finding_tenant ON sup_audit_finding(tenant_id, status);
CREATE INDEX idx_sup_finding_plan ON sup_audit_finding(plan_id);

CREATE TABLE sup_rectification (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    finding_id BIGINT NOT NULL,
    task_title VARCHAR(256),
    assignee_id BIGINT,
    assignee_name VARCHAR(64),
    deadline DATE,
    completed_at TIMESTAMP,
    rectification_measure TEXT,
    result_verification TEXT,
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_sup_rect_finding ON sup_rectification(finding_id);
CREATE INDEX idx_sup_rect_tenant_status ON sup_rectification(tenant_id, status);

CREATE TABLE sup_violation_case (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    case_no VARCHAR(64),
    case_title VARCHAR(256),
    violation_type VARCHAR(32),
    suspect_id BIGINT,
    suspect_name VARCHAR(64),
    asset_loss DECIMAL(18,2),
    investigation_result TEXT,
    punishment_decision TEXT,
    status VARCHAR(32) DEFAULT 'INVESTIGATING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_sup_case_tenant ON sup_violation_case(tenant_id, status);
CREATE INDEX idx_sup_case_no ON sup_violation_case(case_no);
