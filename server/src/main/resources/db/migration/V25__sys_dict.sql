-- P8: 数据字典
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGINT PRIMARY KEY,
    dict_code VARCHAR(64) NOT NULL UNIQUE,
    dict_name VARCHAR(64) NOT NULL,
    status INTEGER DEFAULT 1,
    remark VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_dict_item (
    id BIGINT PRIMARY KEY,
    dict_code VARCHAR(64) NOT NULL,
    item_key VARCHAR(64) NOT NULL,
    item_value VARCHAR(256) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    remark VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dict_item_code ON sys_dict_item(dict_code);

-- 种子数据
INSERT INTO sys_dict_type (id, dict_code, dict_name) VALUES (1, 'ASSET_SOURCE_TYPE', '资产来源类型') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_item (id, dict_code, item_key, item_value, sort_order) VALUES
(1, 'ASSET_SOURCE_TYPE', 'PURCHASE', '采购', 1),
(2, 'ASSET_SOURCE_TYPE', 'LEASE', '租赁', 2),
(3, 'ASSET_SOURCE_TYPE', 'TRANSFER', '调拨', 3),
(4, 'ASSET_SOURCE_TYPE', 'DONATION', '捐赠', 4)
ON CONFLICT DO NOTHING;

INSERT INTO sys_dict_type (id, dict_code, dict_name) VALUES (2, 'USE_STATUS', '使用状态') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_item (id, dict_code, item_key, item_value, sort_order) VALUES
(5, 'USE_STATUS', 'IN_USE', '在用', 1),
(6, 'USE_STATUS', 'IDLE', '闲置', 2),
(7, 'USE_STATUS', 'DISPOSED', '已处置', 3),
(8, 'USE_STATUS', 'RENTED', '出租', 4)
ON CONFLICT DO NOTHING;
