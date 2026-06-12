CREATE TABLE inventory_task (
    id              BIGINT PRIMARY KEY,
    task_name       VARCHAR(256) NOT NULL,
    assignee_id     BIGINT       NOT NULL,
    scope_type      VARCHAR(32)  NOT NULL DEFAULT 'ORG',
    scope_value     VARCHAR(256),
    start_date      DATE,
    end_date        DATE,
    status          VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    total_count     INTEGER      NOT NULL DEFAULT 0,
    completed_count INTEGER      NOT NULL DEFAULT 0,
    diff_count      INTEGER      NOT NULL DEFAULT 0,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted         INTEGER      DEFAULT 0
);

CREATE INDEX idx_inventory_task_assignee ON inventory_task (assignee_id);
CREATE INDEX idx_inventory_task_tenant   ON inventory_task (tenant_id);
CREATE INDEX idx_inventory_task_status   ON inventory_task (status);

CREATE TABLE inventory_record (
    id              BIGINT PRIMARY KEY,
    task_id         BIGINT        NOT NULL,
    asset_id        BIGINT        NOT NULL,
    book_name       VARCHAR(256),
    book_location   VARCHAR(256),
    book_status     VARCHAR(32),
    is_exists       BOOLEAN,
    actual_location VARCHAR(256),
    actual_status   VARCHAR(32),
    remark          TEXT,
    operator_id     BIGINT        NOT NULL,
    tenant_id       BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted         INTEGER       DEFAULT 0
);

CREATE INDEX idx_inventory_record_task  ON inventory_record (task_id);
CREATE INDEX idx_inventory_record_asset ON inventory_record (asset_id);
CREATE INDEX idx_inventory_record_tenant ON inventory_record (tenant_id);

CREATE TABLE inventory_diff (
    id                  BIGINT PRIMARY KEY,
    task_id             BIGINT        NOT NULL,
    record_id           BIGINT,
    asset_id            BIGINT        NOT NULL,
    diff_type           VARCHAR(32)   NOT NULL,
    book_value          VARCHAR(512),
    actual_value        VARCHAR(512),
    description         TEXT,
    status              VARCHAR(16)   NOT NULL DEFAULT 'OPEN',
    approval_instance_id BIGINT,
    tenant_id           BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted             INTEGER       DEFAULT 0
);

CREATE INDEX idx_inventory_diff_task   ON inventory_diff (task_id);
CREATE INDEX idx_inventory_diff_record ON inventory_diff (record_id);
CREATE INDEX idx_inventory_diff_asset  ON inventory_diff (asset_id);
CREATE INDEX idx_inventory_diff_status ON inventory_diff (status);
CREATE INDEX idx_inventory_diff_tenant ON inventory_diff (tenant_id);
