-- V39__property_right.sql
-- 产权管理系统 - 产权占有登记、变动、评估、交易监测、档案

CREATE TABLE pr_registration (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    reg_no VARCHAR(64) NOT NULL,
    reg_type VARCHAR(32) NOT NULL,
    enterprise_name VARCHAR(128) NOT NULL,
    property_type VARCHAR(32),
    property_owner VARCHAR(128),
    equity_pct DECIMAL(5,2),
    registered_capital DECIMAL(18,2),
    paid_capital DECIMAL(18,2),
    registration_date DATE,
    cert_no VARCHAR(128),
    cert_file_ids VARCHAR(512),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pr_reg_tenant ON pr_registration(tenant_id, reg_type, status);
CREATE INDEX idx_pr_reg_org ON pr_registration(org_id);

CREATE TABLE pr_change (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    before_data JSONB,
    after_data JSONB,
    change_reason TEXT,
    approval_file_ids VARCHAR(512),
    effective_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pr_change_reg ON pr_change(registration_id);

CREATE TABLE pr_assessment (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    registration_id BIGINT,
    assess_no VARCHAR(64) NOT NULL,
    assess_purpose VARCHAR(64),
    assess_agency VARCHAR(128),
    assess_method VARCHAR(32),
    book_value DECIMAL(18,2),
    assessed_value DECIMAL(18,2),
    deviation_pct DECIMAL(5,2),
    assess_report_ids VARCHAR(512),
    approval_status VARCHAR(32) DEFAULT 'PENDING',
    assess_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pr_assess_tenant ON pr_assessment(tenant_id, approval_status);

CREATE TABLE pr_transaction_monitor (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    exchange_name VARCHAR(128),
    listing_no VARCHAR(64),
    project_name VARCHAR(256),
    listing_price DECIMAL(18,2),
    bid_start_date DATE,
    bid_end_date DATE,
    transaction_price DECIMAL(18,2),
    buyer_name VARCHAR(128),
    price_deviation_pct DECIMAL(5,2),
    is_abnormal BOOLEAN DEFAULT FALSE,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pr_trans_tenant ON pr_transaction_monitor(tenant_id, is_abnormal);

CREATE TABLE pr_archive (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    ref_type VARCHAR(32),
    ref_id BIGINT,
    doc_name VARCHAR(256),
    doc_type VARCHAR(64),
    file_path VARCHAR(512),
    file_size BIGINT,
    uploader_id BIGINT,
    uploader_name VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_pr_archive_ref ON pr_archive(ref_type, ref_id);
