-- ============================================================
-- M10: 重大事项管理 (Major Event Management)
-- Tables: me_event, me_lawsuit, me_guarantee
-- ============================================================

CREATE TABLE me_event (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    event_no VARCHAR(64) NOT NULL,
    event_type VARCHAR(32),
    title VARCHAR(256) NOT NULL,
    description TEXT,
    impact_assessment TEXT,
    handling_plan TEXT,
    approval_instance_id BIGINT,
    status VARCHAR(32) DEFAULT 'REPORTED',
    reported_at TIMESTAMP,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_me_event_tenant ON me_event(tenant_id, status);
CREATE INDEX idx_me_event_no ON me_event(event_no);
CREATE INDEX idx_me_event_org ON me_event(org_id);

CREATE TABLE me_lawsuit (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    event_id BIGINT,
    case_no VARCHAR(128),
    court VARCHAR(128),
    plaintiff VARCHAR(128),
    defendant VARCHAR(128),
    claim_amount DECIMAL(18,2),
    judgment_amount DECIMAL(18,2),
    law_firm VARCHAR(128),
    attorney VARCHAR(64),
    trial_progress TEXT,
    judgment_date DATE,
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_me_lawsuit_tenant ON me_lawsuit(tenant_id);
CREATE INDEX idx_me_lawsuit_event ON me_lawsuit(event_id);
CREATE INDEX idx_me_lawsuit_status ON me_lawsuit(status);

CREATE TABLE me_guarantee (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    event_id BIGINT,
    guarantee_type VARCHAR(32),
    beneficiary VARCHAR(128),
    guarantee_amount DECIMAL(18,2),
    guarantee_period_start DATE,
    guarantee_period_end DATE,
    counter_guarantee TEXT,
    risk_level VARCHAR(16),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_me_guarantee_tenant ON me_guarantee(tenant_id);
CREATE INDEX idx_me_guarantee_event ON me_guarantee(event_id);
CREATE INDEX idx_me_guarantee_expiring ON me_guarantee(tenant_id, guarantee_period_end, status);
