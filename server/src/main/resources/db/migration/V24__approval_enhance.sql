-- P7: 流程引擎增强（会签/加签/条件路由/超时升级）
-- 审批节点增加会签模式和加签开关
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS approve_mode VARCHAR(16) DEFAULT 'SINGLE';
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS allow_add_sign BOOLEAN DEFAULT TRUE;
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS timeout_action VARCHAR(16) DEFAULT 'ESCALATE';
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS condition_type VARCHAR(16) DEFAULT NULL;
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS condition_value VARCHAR(256) DEFAULT NULL;
ALTER TABLE approval_node ADD COLUMN IF NOT EXISTS escalate_role VARCHAR(64) DEFAULT NULL;

COMMENT ON COLUMN approval_node.approve_mode IS '审批模式：SINGLE-单人/COUNTER_SIGN-会签/OR_SIGN-或签';
COMMENT ON COLUMN approval_node.allow_add_sign IS '是否允许加签';
COMMENT ON COLUMN approval_node.timeout_action IS '超时动作：ESCALATE-升级/NOTIFY-通知/AUTO_APPROVE-自动通过/AUTO_REJECT-自动驳回';
COMMENT ON COLUMN approval_node.condition_type IS '条件类型：AMOUNT_GT-金额大于/AMOUNT_LT-金额小于/BIZ_TYPE-业务类型';
COMMENT ON COLUMN approval_node.condition_value IS '条件值（如100000表示金额>10万时启用此节点）';
COMMENT ON COLUMN approval_node.escalate_role IS '超时升级目标角色编码';

-- 加签记录表
CREATE TABLE IF NOT EXISTS approval_add_sign (
    id BIGINT PRIMARY KEY,
    instance_id BIGINT NOT NULL,
    node_order INTEGER NOT NULL,
    add_sign_user_id BIGINT NOT NULL,
    add_sign_user_name VARCHAR(64),
    reason VARCHAR(500),
    approved BOOLEAN,
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_add_sign_instance ON approval_add_sign(instance_id, node_order);

COMMENT ON TABLE approval_add_sign IS '加签记录表（动态添加审批人）';

-- 超时处理记录表
CREATE TABLE IF NOT EXISTS approval_timeout_record (
    id BIGINT PRIMARY KEY,
    instance_id BIGINT NOT NULL,
    node_order INTEGER NOT NULL,
    action_taken VARCHAR(16),
    escalate_to_role VARCHAR(64),
    escalate_to_user_id BIGINT,
    handled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_timeout_instance ON approval_timeout_record(instance_id);

COMMENT ON TABLE approval_timeout_record IS '审批超时处理记录';
