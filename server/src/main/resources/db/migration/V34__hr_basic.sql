-- V2__hr_basic.sql
-- HR基础花名册 - 人员档案、异动记录、合同管理

CREATE TABLE hr_employee (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    employee_no VARCHAR(32) NOT NULL,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(8),
    id_card VARCHAR(20),
    birth_date DATE,
    phone VARCHAR(20),
    email VARCHAR(128),
    education VARCHAR(32),
    major VARCHAR(64),
    graduate_school VARCHAR(128),
    entry_date DATE,
    work_years INTEGER DEFAULT 0,
    dept_id BIGINT,
    position VARCHAR(64),
    title VARCHAR(32),
    employment_type VARCHAR(32) DEFAULT 'FULL_TIME',
    status VARCHAR(32) DEFAULT 'ACTIVE',
    avatar_url VARCHAR(256),
    emergency_contact VARCHAR(64),
    emergency_phone VARCHAR(20),
    home_address VARCHAR(256),
    remark VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX idx_hr_employee_no ON hr_employee(tenant_id, employee_no) WHERE deleted = 0;
CREATE INDEX idx_hr_employee_org ON hr_employee(tenant_id, org_id) WHERE deleted = 0;
CREATE INDEX idx_hr_employee_dept ON hr_employee(tenant_id, dept_id) WHERE deleted = 0;

CREATE TABLE hr_employee_change (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    before_value JSONB,
    after_value JSONB,
    effective_date DATE NOT NULL,
    reason VARCHAR(512),
    approval_instance_id BIGINT,
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_hr_change_employee ON hr_employee_change(tenant_id, employee_id);
CREATE INDEX idx_hr_change_type ON hr_employee_change(tenant_id, change_type);

CREATE TABLE hr_contract (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    contract_no VARCHAR(64) NOT NULL,
    contract_type VARCHAR(32) DEFAULT 'LABOR',
    sign_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    is_unlimited BOOLEAN DEFAULT FALSE,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    terms_summary TEXT,
    attachment_ids VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_hr_contract_employee ON hr_contract(tenant_id, employee_id);
CREATE INDEX idx_hr_contract_end_date ON hr_contract(tenant_id, end_date) WHERE deleted = 0;
