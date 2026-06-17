-- V40__finance.sql
-- 财务监督系统 - 财务报表、指标、资金监控、预算执行

CREATE TABLE fin_report (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    report_type VARCHAR(32) NOT NULL,
    report_year INTEGER NOT NULL,
    report_period INTEGER NOT NULL,
    report_data JSONB NOT NULL,
    status VARCHAR(32) DEFAULT 'DRAFT',
    submitted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_fin_report_tenant ON fin_report(tenant_id, report_type, status);
CREATE INDEX idx_fin_report_org ON fin_report(org_id);

CREATE TABLE fin_indicator (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    indicator_code VARCHAR(64) NOT NULL,
    indicator_name VARCHAR(128),
    indicator_value DECIMAL(18,4),
    threshold_warn DECIMAL(18,4),
    threshold_alarm DECIMAL(18,4),
    period_year INTEGER,
    period_month INTEGER,
    status VARCHAR(32) DEFAULT 'NORMAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_fin_indicator_tenant ON fin_indicator(tenant_id, indicator_code, period_year);
CREATE INDEX idx_fin_indicator_org ON fin_indicator(org_id);

CREATE TABLE fin_fund_monitor (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    transaction_no VARCHAR(128),
    transaction_date TIMESTAMP,
    amount DECIMAL(18,2),
    payer VARCHAR(128),
    payee VARCHAR(128),
    purpose VARCHAR(256),
    is_flagged BOOLEAN DEFAULT FALSE,
    flag_reason VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_fin_fund_tenant ON fin_fund_monitor(tenant_id, is_flagged);
CREATE INDEX idx_fin_fund_date ON fin_fund_monitor(tenant_id, transaction_date);

CREATE TABLE fin_budget (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    budget_year INTEGER NOT NULL,
    budget_type VARCHAR(32),
    planned_amount DECIMAL(18,2),
    actual_amount DECIMAL(18,2) DEFAULT 0,
    executed_pct DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_fin_budget_tenant ON fin_budget(tenant_id, budget_year);
CREATE INDEX idx_fin_budget_org ON fin_budget(org_id);
