-- V30: 新增报表数据源实体表（股权、债权、私募基金、特许经营权、数据资产、自然资源、货币资金）
-- 为报表导出/导入功能提供独立的数据存储，与 asset 表解耦

-- 股权类投资
CREATE TABLE IF NOT EXISTS equity_investment (
    id BIGINT PRIMARY KEY,
    enterprise_name VARCHAR(200) NOT NULL,
    credit_code VARCHAR(50),
    invest_date DATE,
    invest_method VARCHAR(50),
    share_ratio DECIMAL(5,2),
    invest_amount DECIMAL(18,2),
    cumulative_dividend DECIMAL(18,2),
    book_value DECIMAL(18,2),
    fair_value DECIMAL(18,2),
    is_impaired BOOLEAN DEFAULT FALSE,
    impairment_amount DECIMAL(18,2),
    enterprise_status VARCHAR(50),
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 债权
CREATE TABLE IF NOT EXISTS creditor_rights (
    id BIGINT PRIMARY KEY,
    creditor_code VARCHAR(100),
    debtor_name VARCHAR(200) NOT NULL,
    rights_type VARCHAR(50) NOT NULL,
    amount DECIMAL(18,2),
    formed_date DATE,
    aging VARCHAR(50),
    bad_debt_provision DECIMAL(18,2),
    estimated_recoverable DECIMAL(18,2),
    collection_status VARCHAR(50),
    contract_no VARCHAR(100),
    is_litigation BOOLEAN DEFAULT FALSE,
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 私募基金投资
CREATE TABLE IF NOT EXISTS pe_fund_investment (
    id BIGINT PRIMARY KEY,
    fund_name VARCHAR(200) NOT NULL,
    fund_manager VARCHAR(200),
    fund_type VARCHAR(50),
    subscribed_amount DECIMAL(18,2),
    paid_amount DECIMAL(18,2),
    invest_date DATE,
    fund_duration VARCHAR(50),
    current_nav DECIMAL(18,2),
    cumulative_return DECIMAL(18,2),
    is_violation BOOLEAN DEFAULT FALSE,
    record_no VARCHAR(100),
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 特许经营权
CREATE TABLE IF NOT EXISTS franchise_right (
    id BIGINT PRIMARY KEY,
    right_name VARCHAR(200) NOT NULL,
    authorizer VARCHAR(200),
    start_date DATE,
    end_date DATE,
    authorized_area VARCHAR(200),
    business_scope VARCHAR(500),
    authorization_fee DECIMAL(18,2),
    annual_fee DECIMAL(18,2),
    is_expired BOOLEAN DEFAULT FALSE,
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 数据资源/资产
CREATE TABLE IF NOT EXISTS data_asset (
    id BIGINT PRIMARY KEY,
    data_name VARCHAR(200) NOT NULL,
    data_type VARCHAR(100),
    data_volume VARCHAR(100),
    storage_method VARCHAR(100),
    security_level VARCHAR(50),
    is_in_balance_sheet BOOLEAN DEFAULT FALSE,
    balance_sheet_value DECIMAL(18,2),
    usage_scenario VARCHAR(500),
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 自然资源资产
CREATE TABLE IF NOT EXISTS natural_resource (
    id BIGINT PRIMARY KEY,
    resource_name VARCHAR(200) NOT NULL,
    resource_type VARCHAR(100),
    location VARCHAR(300),
    area_or_reserve VARCHAR(100),
    unit VARCHAR(50),
    certificate_no VARCHAR(100),
    acquisition_method VARCHAR(50),
    useful_life_years INTEGER,
    book_value DECIMAL(18,2),
    appraised_value DECIMAL(18,2),
    is_developed BOOLEAN DEFAULT FALSE,
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 货币资金账户
CREATE TABLE IF NOT EXISTS cash_account (
    id BIGINT PRIMARY KEY,
    account_name VARCHAR(200) NOT NULL,
    bank_name VARCHAR(200),
    account_no VARCHAR(100),
    currency VARCHAR(10) DEFAULT 'CNY',
    book_balance DECIMAL(18,2),
    statement_balance DECIMAL(18,2),
    diff_amount DECIMAL(18,2),
    diff_reason VARCHAR(500),
    account_type VARCHAR(50),
    is_restricted BOOLEAN DEFAULT FALSE,
    restricted_amount DECIMAL(18,2),
    reconciliation_date DATE,
    org_id BIGINT,
    tenant_id BIGINT DEFAULT 0,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
