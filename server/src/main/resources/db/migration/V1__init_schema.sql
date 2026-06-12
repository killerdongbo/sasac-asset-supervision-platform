CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(64),
    phone VARCHAR(20),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    org_id BIGINT,
    role_code VARCHAR(32) NOT NULL DEFAULT 'ENTERPRISE_OPERATOR',
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_sys_user_tenant ON sys_user(tenant_id);
CREATE INDEX idx_sys_user_username ON sys_user(username);

-- Default admin user: username=admin, password=admin123 (BCrypt encoded)
INSERT INTO sys_user (id, username, password, real_name, tenant_id, role_code, status)
VALUES (1, 'admin',
        '$2a$10$70maX0i61VQF7IamHwYQKuBLC6BbNCW2LLfnpOZDkAYPGrJ7.8FFi',
        '系统管理员', 0, 'SYSTEM_ADMIN', 1);
