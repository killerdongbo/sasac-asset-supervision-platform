-- P9: 消息通知系统
CREATE TABLE IF NOT EXISTS sys_notification (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT,
    title VARCHAR(256) NOT NULL,
    content TEXT,
    type VARCHAR(32) NOT NULL DEFAULT 'SYSTEM',
    level VARCHAR(16) DEFAULT 'INFO',
    biz_type VARCHAR(64),
    biz_id BIGINT,
    is_read INTEGER DEFAULT 0,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notif_user ON sys_notification(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notif_tenant ON sys_notification(tenant_id, created_at);

COMMENT ON TABLE sys_notification IS '消息通知表';
COMMENT ON COLUMN sys_notification.type IS '消息类型：SYSTEM/APPROVAL/ALERT/REPORT';
COMMENT ON COLUMN sys_notification.level IS '级别：INFO/WARNING/CRITICAL';
COMMENT ON COLUMN sys_notification.is_read IS '0-未读 1-已读';

CREATE TABLE IF NOT EXISTS sys_notification_template (
    id BIGINT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL UNIQUE,
    template_name VARCHAR(128),
    title_tpl VARCHAR(256),
    content_tpl TEXT,
    channels VARCHAR(128) DEFAULT 'IN_APP',
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_notification_template IS '消息模板表';
COMMENT ON COLUMN sys_notification_template.channels IS '发送渠道：IN_APP/WECHAT/SMS，逗号分隔';

-- 种子模板
INSERT INTO sys_notification_template (id, template_code, template_name, title_tpl, content_tpl, channels)
VALUES (1, 'APPROVAL_PENDING', '待审批通知', '您有新的审批待处理', '业务类型：{bizType}，提交人：{submitter}，请及时处理', 'IN_APP')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_notification_template (id, template_code, template_name, title_tpl, content_tpl, channels)
VALUES (2, 'APPROVAL_RESULT', '审批结果通知', '您的申请已{result}', '业务类型：{bizType}，审批结果：{result}，审批人：{approver}', 'IN_APP')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_notification_template (id, template_code, template_name, title_tpl, content_tpl, channels)
VALUES (3, 'ALERT_NEW', '新预警通知', '预警：{title}', '资产：{assetName}，预警类型：{alertType}，请及时排查', 'IN_APP,WECHAT')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_notification_template (id, template_code, template_name, title_tpl, content_tpl, channels)
VALUES (4, 'ASSET_EXPIRE', '资产到期提醒', '资产即将过期：{assetName}', '资产编码：{assetCode}，到期日期：{expireDate}，请及时处理', 'IN_APP')
ON CONFLICT (id) DO NOTHING;
