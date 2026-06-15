-- P4: 多租户管理
-- 租户主表
CREATE TABLE IF NOT EXISTS sys_tenant (
    id BIGINT PRIMARY KEY,
    tenant_code VARCHAR(64) NOT NULL UNIQUE,
    tenant_name VARCHAR(128) NOT NULL,
    contact_person VARCHAR(64),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(128),
    status INTEGER DEFAULT 1,
    expire_time TIMESTAMP,
    max_users INTEGER DEFAULT 50,
    max_assets INTEGER DEFAULT 10000,
    edition VARCHAR(32) DEFAULT 'STANDARD',
    logo_url VARCHAR(512),
    domain VARCHAR(128),
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

COMMENT ON TABLE sys_tenant IS '租户信息表';
COMMENT ON COLUMN sys_tenant.tenant_code IS '租户编码（唯一标识）';
COMMENT ON COLUMN sys_tenant.status IS '状态：1-正常，0-禁用，2-过期';
COMMENT ON COLUMN sys_tenant.edition IS '版本：BASIC/STANDARD/ENTERPRISE';
COMMENT ON COLUMN sys_tenant.max_users IS '最大用户数';
COMMENT ON COLUMN sys_tenant.max_assets IS '最大资产数';

-- 租户配置表（KV形式，灵活扩展）
CREATE TABLE IF NOT EXISTS sys_tenant_config (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    config_key VARCHAR(128) NOT NULL,
    config_value TEXT,
    description VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, config_key)
);

COMMENT ON TABLE sys_tenant_config IS '租户配置表';

-- 租户用量统计表（定时更新）
CREATE TABLE IF NOT EXISTS sys_tenant_usage (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    user_count INTEGER DEFAULT 0,
    asset_count INTEGER DEFAULT 0,
    storage_used_mb BIGINT DEFAULT 0,
    last_login_time TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_tenant_usage IS '租户用量统计';

-- 初始化默认租户
INSERT INTO sys_tenant (id, tenant_code, tenant_name, contact_person, status, edition, max_users, max_assets)
VALUES (1, 'default', '湛江市国资委', '管理员', 1, 'ENTERPRISE', 999, 999999)
ON CONFLICT (id) DO NOTHING;

-- 为现有用户关联默认租户
UPDATE sys_user SET tenant_id = 1 WHERE tenant_id IS NULL OR tenant_id = 0;

CREATE INDEX IF NOT EXISTS idx_tenant_config_tid ON sys_tenant_config(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tenant_usage_tid ON sys_tenant_usage(tenant_id);
