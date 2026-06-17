-- V41__performance.sql
-- 业绩考核与薪酬管理 - 考核指标、薪酬预算、股权激励

CREATE TABLE perf_indicator_def (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    indicator_code VARCHAR(64) NOT NULL,
    indicator_name VARCHAR(128),
    category VARCHAR(32),
    weight DECIMAL(5,2),
    target_value DECIMAL(18,4),
    actual_value DECIMAL(18,4),
    score DECIMAL(5,1),
    cycle_year INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_perf_indicator_tenant ON perf_indicator_def(tenant_id, indicator_code, cycle_year);
CREATE INDEX idx_perf_indicator_org ON perf_indicator_def(org_id);

CREATE TABLE perf_salary_budget (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    budget_year INTEGER NOT NULL,
    total_budget DECIMAL(18,2),
    approved_budget DECIMAL(18,2),
    actual_paid DECIMAL(18,2),
    adjustment_reason TEXT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_perf_salary_budget_tenant ON perf_salary_budget(tenant_id, budget_year);
CREATE INDEX idx_perf_salary_budget_org ON perf_salary_budget(org_id);

CREATE TABLE perf_incentive (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    incentive_type VARCHAR(32),
    employee_id BIGINT,
    grant_date DATE,
    vesting_period INTEGER,
    amount DECIMAL(18,2),
    status VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_perf_incentive_tenant ON perf_incentive(tenant_id, incentive_type);
CREATE INDEX idx_perf_incentive_employee ON perf_incentive(employee_id);
