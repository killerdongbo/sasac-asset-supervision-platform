-- V37__investment.sql
-- Investment management core tables

CREATE TABLE invest_plan (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    plan_year INTEGER NOT NULL,
    plan_name VARCHAR(256),
    total_budget DECIMAL(18,2),
    approved_by VARCHAR(64),
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_plan_year ON invest_plan(tenant_id, plan_year);

CREATE TABLE invest_project (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    plan_id BIGINT,
    project_no VARCHAR(64) NOT NULL,
    project_name VARCHAR(256),
    invest_type VARCHAR(32),
    industry VARCHAR(64),
    region VARCHAR(64),
    invest_amount DECIMAL(18,2),
    equity_pct DECIMAL(5,2),
    target_company VARCHAR(128),
    target_description TEXT,
    decision_item_id BIGINT,
    approval_instance_id BIGINT,
    phase VARCHAR(32) DEFAULT 'PRE_INVEST',
    expected_roi DECIMAL(5,2),
    actual_roi DECIMAL(5,2),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_project_tenant ON invest_project(tenant_id, status);
CREATE INDEX idx_invest_project_phase ON invest_project(tenant_id, phase);

CREATE TABLE invest_dd (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    dd_type VARCHAR(32),
    law_firm VARCHAR(128),
    accounting_firm VARCHAR(128),
    report_summary TEXT,
    risk_findings TEXT,
    attachment_ids VARCHAR(512),
    status VARCHAR(32) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_dd_project ON invest_dd(tenant_id, project_id);

CREATE TABLE invest_deal (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    deal_date DATE,
    deal_amount DECIMAL(18,2),
    equity_acquired DECIMAL(5,2),
    payment_terms TEXT,
    agreement_no VARCHAR(128),
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_deal_project ON invest_deal(tenant_id, project_id);

CREATE TABLE invest_post (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    report_date DATE,
    revenue DECIMAL(18,2),
    net_profit DECIMAL(18,2),
    net_assets DECIMAL(18,2),
    debt_ratio DECIMAL(5,2),
    major_events TEXT,
    risk_level VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_post_project ON invest_post(tenant_id, project_id, report_date);

CREATE TABLE invest_exit (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    exit_date DATE,
    exit_amount DECIMAL(18,2),
    exit_method VARCHAR(32),
    return_rate DECIMAL(5,2),
    profit_loss DECIMAL(18,2),
    approval_instance_id BIGINT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_exit_project ON invest_exit(tenant_id, project_id);

CREATE TABLE invest_equity_node (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT,
    company_name VARCHAR(128) NOT NULL,
    parent_id BIGINT,
    equity_pct DECIMAL(5,2),
    level INTEGER DEFAULT 0,
    is_actual_controller BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_invest_equity_project ON invest_equity_node(tenant_id, project_id);
CREATE INDEX idx_invest_equity_parent ON invest_equity_node(parent_id);
