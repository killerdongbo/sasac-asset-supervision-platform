-- V38__hr_deep.sql
-- HR深度人事 - 薪资、考勤、绩效、招聘、培训

CREATE TABLE hr_salary (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    salary_year INTEGER NOT NULL,
    salary_month INTEGER NOT NULL,
    base_salary DECIMAL(12,2),
    performance_pay DECIMAL(12,2),
    overtime_pay DECIMAL(12,2),
    allowance DECIMAL(12,2),
    deduction DECIMAL(12,2),
    social_insurance DECIMAL(12,2),
    housing_fund DECIMAL(12,2),
    tax DECIMAL(12,2),
    net_salary DECIMAL(12,2),
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_hr_salary_employee ON hr_salary(tenant_id, employee_id, salary_year, salary_month);

CREATE TABLE hr_attendance (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    att_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    status VARCHAR(32) DEFAULT 'NORMAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE UNIQUE INDEX idx_hr_att_date ON hr_attendance(tenant_id, employee_id, att_date);

CREATE TABLE hr_performance (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    cycle_type VARCHAR(32),
    cycle_year INTEGER,
    kpi_items JSONB,
    self_score DECIMAL(5,1),
    manager_score DECIMAL(5,1),
    final_score DECIMAL(5,1),
    grade VARCHAR(8),
    review_comment TEXT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE hr_recruitment (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    position_name VARCHAR(128),
    headcount INTEGER DEFAULT 1,
    pipeline VARCHAR(32),
    candidates TEXT,
    status VARCHAR(32) DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE hr_training (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    course_name VARCHAR(256),
    trainer VARCHAR(64),
    training_date DATE,
    duration_hours DECIMAL(4,1),
    attendees TEXT,
    evaluation_summary TEXT,
    attachment_ids VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
