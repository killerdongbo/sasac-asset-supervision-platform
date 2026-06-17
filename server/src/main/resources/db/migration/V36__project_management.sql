-- V36__project_management.sql
-- Project management core tables

CREATE TABLE pm_project (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    project_no VARCHAR(64) NOT NULL,
    name VARCHAR(256) NOT NULL,
    project_type VARCHAR(32),
    category VARCHAR(32),
    budget_total DECIMAL(18,2),
    budget_approved DECIMAL(18,2),
    start_date DATE,
    planned_end_date DATE,
    actual_end_date DATE,
    manager_id BIGINT,
    manager_name VARCHAR(64),
    department VARCHAR(128),
    decision_item_id BIGINT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    description TEXT,
    remark VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pm_project_status ON pm_project(tenant_id, status);

CREATE TABLE pm_budget (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    budget_year INTEGER NOT NULL,
    category VARCHAR(64),
    planned_amount DECIMAL(18,2),
    actual_amount DECIMAL(18,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE pm_progress (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    report_date DATE NOT NULL,
    reporter_id BIGINT,
    reporter_name VARCHAR(64),
    progress_pct DECIMAL(5,2),
    completed_work TEXT,
    pending_work TEXT,
    issues TEXT,
    next_plan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pm_progress_project ON pm_progress(tenant_id, project_id, report_date);

CREATE TABLE pm_contract (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    contract_no VARCHAR(64) NOT NULL,
    contract_name VARCHAR(256),
    counterparty VARCHAR(128),
    amount DECIMAL(18,2),
    sign_date DATE,
    start_date DATE,
    end_date DATE,
    payment_terms TEXT,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE pm_document (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    doc_name VARCHAR(256),
    doc_type VARCHAR(64),
    file_path VARCHAR(512),
    file_size BIGINT,
    uploader_id BIGINT,
    uploader_name VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE pm_acceptance (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    acceptance_no VARCHAR(64),
    acceptance_type VARCHAR(32),
    result VARCHAR(32),
    score DECIMAL(5,1),
    review_opinion TEXT,
    reviewer_id BIGINT,
    reviewer_name VARCHAR(64),
    acceptance_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
