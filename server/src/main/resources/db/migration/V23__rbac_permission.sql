-- P6: 权限管理（RBAC精细化权限体系）
-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    role_code VARCHAR(64) NOT NULL,
    role_name VARCHAR(64) NOT NULL,
    role_type VARCHAR(32) DEFAULT 'CUSTOM',
    data_scope VARCHAR(32) DEFAULT 'ORG',
    description VARCHAR(256),
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    UNIQUE(tenant_id, role_code)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.role_type IS '角色类型：SYSTEM-内置/CUSTOM-自定义';
COMMENT ON COLUMN sys_role.data_scope IS '数据权限范围：ALL-全部/TENANT-本租户/ORG-本组织/ORG_AND_SUB-本组织及下级/SELF-仅本人';

-- 权限表（菜单 + 按钮）
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    perm_code VARCHAR(128) NOT NULL UNIQUE,
    perm_name VARCHAR(64) NOT NULL,
    perm_type VARCHAR(16) NOT NULL DEFAULT 'MENU',
    path VARCHAR(256),
    icon VARCHAR(64),
    sort_order INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_permission IS '权限表';
COMMENT ON COLUMN sys_permission.perm_type IS '权限类型：MENU-菜单/BUTTON-按钮/API-接口';
COMMENT ON COLUMN sys_permission.perm_code IS '权限编码（如 asset:create / asset:delete，菜单用路由路径）';

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    perm_id BIGINT NOT NULL,
    UNIQUE(role_id, perm_id)
);

CREATE INDEX IF NOT EXISTS idx_rp_role ON sys_role_permission(role_id);

-- 用户-角色关联表（支持一个用户多角色）
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE(user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_ur_user ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_ur_role ON sys_user_role(role_id);

-- ===== 初始化内置角色 =====
-- 超管角色（tenantId=0, 全局可见）
INSERT INTO sys_role (id, tenant_id, role_code, role_name, role_type, data_scope, description, status)
VALUES (1, 0, 'SYSTEM_ADMIN', '系统超级管理员', 'SYSTEM', 'ALL', '平台最高权限，管理所有租户', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_role (id, tenant_id, role_code, role_name, role_type, data_scope, description, status)
VALUES (2, 0, 'TENANT_ADMIN', '租户管理员', 'SYSTEM', 'TENANT', '管理本租户的组织/用户/角色/权限', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_role (id, tenant_id, role_code, role_name, role_type, data_scope, description, status)
VALUES (3, 0, 'ORG_MANAGER', '组织管理员', 'SYSTEM', 'ORG_AND_SUB', '管理本组织及下级组织的资产和数据', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_role (id, tenant_id, role_code, role_name, role_type, data_scope, description, status)
VALUES (4, 0, 'ENTERPRISE_OPERATOR', '企业操作员', 'SYSTEM', 'ORG', '日常资产操作：录入/修改/查询', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_role (id, tenant_id, role_code, role_name, role_type, data_scope, description, status)
VALUES (5, 0, 'VIEWER', '只读用户', 'SYSTEM', 'TENANT', '仅查看权限，不可修改', 1)
ON CONFLICT (id) DO NOTHING;

-- ===== 初始化权限 =====
-- 菜单权限
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (1, 0, '/dashboard', '管理驾驶舱', 'MENU', '/', 'DataAnalysis', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (2, 0, '/assets', '资产清单', 'MENU', '/assets', 'Notebook', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (3, 0, '/circulation', '资产流转', 'MENU', '', 'Refresh', 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (4, 0, '/procurement', '采购管理', 'MENU', '', 'ShoppingCart', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (5, 0, '/field', '现场管理', 'MENU', '', 'Monitor', 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (6, 0, '/approval', '审批工作流', 'MENU', '', 'DocumentChecked', 6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (7, 0, '/supervision', '统计报表', 'MENU', '', 'DataLine', 7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (8, 0, '/quotation', '报价分析', 'MENU', '', 'Coin', 8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (9, 0, '/system', '系统管理', 'MENU', '', 'Setting', 9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (10, 0, '/screen', '数据大屏', 'MENU', '/screen', 'DataBoard', 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (11, 0, '/audit', '审计追溯', 'MENU', '', 'Warning', 11)
ON CONFLICT (id) DO NOTHING;

-- 按钮/操作权限
INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (20, 2, 'asset:create', '新增资产', 'BUTTON', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (21, 2, 'asset:edit', '编辑资产', 'BUTTON', NULL, NULL, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (22, 2, 'asset:delete', '删除资产', 'BUTTON', NULL, NULL, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (23, 2, 'asset:export', '导出资产', 'BUTTON', NULL, NULL, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (24, 9, 'tenant:manage', '租户管理', 'BUTTON', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (25, 9, 'user:manage', '用户管理', 'BUTTON', NULL, NULL, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (26, 9, 'role:manage', '角色管理', 'BUTTON', NULL, NULL, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (27, 3, 'stock-in:manage', '入库管理', 'BUTTON', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (28, 3, 'assignment:manage', '领用管理', 'BUTTON', NULL, NULL, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (29, 3, 'transfer:manage', '调拨管理', 'BUTTON', NULL, NULL, 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (30, 3, 'disposal:manage', '报废管理', 'BUTTON', NULL, NULL, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (31, 5, 'inspection:manage', '巡检管理', 'BUTTON', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (32, 5, 'inventory:manage', '盘点管理', 'BUTTON', NULL, NULL, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, icon, sort_order)
VALUES (33, 5, 'maintenance:manage', '维保管理', 'BUTTON', NULL, NULL, 3)
ON CONFLICT (id) DO NOTHING;

-- ===== 为超管分配角色 =====
INSERT INTO sys_user_role (id, user_id, role_id) VALUES (1, 1, 1)
ON CONFLICT (id) DO NOTHING;

-- ===== 为 SYSTEM_ADMIN 角色分配全部菜单权限 =====
INSERT INTO sys_role_permission (id, role_id, perm_id)
SELECT (permission_id * 1000 + 1), 1, permission_id
FROM (SELECT id AS permission_id FROM sys_permission WHERE id BETWEEN 1 AND 11) AS perms
ON CONFLICT (id) DO NOTHING;
