-- 流程定义表
CREATE TABLE IF NOT EXISTS workflow_def (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(256) NOT NULL,
    biz_type    VARCHAR(64)  NOT NULL,
    graph_json  TEXT,
    status      VARCHAR(32)  DEFAULT 'ACTIVE',
    description TEXT,
    tenant_id   BIGINT       DEFAULT 1,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted     INTEGER      DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_wf_def_tenant   ON workflow_def (tenant_id);
CREATE INDEX IF NOT EXISTS idx_wf_def_biz_type ON workflow_def (biz_type);

-- 流程实例表
CREATE TABLE IF NOT EXISTS workflow_instance (
    id               BIGINT PRIMARY KEY,
    def_id           BIGINT        NOT NULL,
    biz_id           BIGINT,
    biz_type         VARCHAR(64),
    status           VARCHAR(32)   DEFAULT 'PENDING',
    current_node_ids VARCHAR(1024),
    context_json     TEXT,
    submitter_id     BIGINT,
    tenant_id        BIGINT        DEFAULT 1,
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted          INTEGER       DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_wf_inst_def    ON workflow_instance (def_id);
CREATE INDEX IF NOT EXISTS idx_wf_inst_status ON workflow_instance (status);
CREATE INDEX IF NOT EXISTS idx_wf_inst_tenant ON workflow_instance (tenant_id);

-- 审批任务表
CREATE TABLE IF NOT EXISTS workflow_task (
    id            BIGINT PRIMARY KEY,
    instance_id   BIGINT        NOT NULL,
    node_id       VARCHAR(64)   NOT NULL,
    approver_role VARCHAR(128),
    approver_id   BIGINT,
    action        VARCHAR(32)   DEFAULT 'PENDING',
    remark        TEXT,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP,
    deleted       INTEGER       DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_wf_task_instance ON workflow_task (instance_id);
CREATE INDEX IF NOT EXISTS idx_wf_task_action   ON workflow_task (action);
