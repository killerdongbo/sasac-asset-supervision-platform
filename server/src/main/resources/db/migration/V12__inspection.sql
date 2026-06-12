CREATE TABLE inspection_task (
    id          BIGINT PRIMARY KEY,
    task_name   VARCHAR(256)  NOT NULL,
    assignee_id BIGINT        NOT NULL,
    asset_scope TEXT          NOT NULL,
    start_date  DATE,
    end_date    DATE,
    status      VARCHAR(32)   NOT NULL DEFAULT 'PENDING',
    total_count INTEGER       NOT NULL DEFAULT 0,
    completed_count INTEGER   NOT NULL DEFAULT 0,
    tenant_id   BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted     INTEGER       DEFAULT 0
);

CREATE INDEX idx_inspection_task_assignee ON inspection_task (assignee_id);
CREATE INDEX idx_inspection_task_tenant   ON inspection_task (tenant_id);
CREATE INDEX idx_inspection_task_status   ON inspection_task (status);

CREATE TABLE inspection_record (
    id              BIGINT PRIMARY KEY,
    task_id         BIGINT  NOT NULL,
    asset_id        BIGINT  NOT NULL,
    is_normal       BOOLEAN,
    actual_location VARCHAR(256),
    actual_status   VARCHAR(32),
    anomaly_type    VARCHAR(32),
    description     TEXT,
    photo_ids       TEXT,
    inspector_id    BIGINT  NOT NULL,
    tenant_id       BIGINT  NOT NULL DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted         INTEGER       DEFAULT 0
);

CREATE INDEX idx_inspection_record_task   ON inspection_record (task_id);
CREATE INDEX idx_inspection_record_asset  ON inspection_record (asset_id);
CREATE INDEX idx_inspection_record_tenant ON inspection_record (tenant_id);

CREATE TABLE inspection_anomaly (
    id                    BIGINT PRIMARY KEY,
    task_id               BIGINT       NOT NULL,
    record_id             BIGINT       NOT NULL,
    asset_id              BIGINT       NOT NULL,
    anomaly_type          VARCHAR(32)  NOT NULL,
    description           TEXT,
    resolution            VARCHAR(32),
    maintenance_request_id VARCHAR(128),
    status                VARCHAR(32)  NOT NULL DEFAULT 'OPEN',
    tenant_id             BIGINT       NOT NULL DEFAULT 0,
    created_at            TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted               INTEGER      DEFAULT 0
);

CREATE INDEX idx_inspection_anomaly_task   ON inspection_anomaly (task_id);
CREATE INDEX idx_inspection_anomaly_record ON inspection_anomaly (record_id);
CREATE INDEX idx_inspection_anomaly_asset  ON inspection_anomaly (asset_id);
CREATE INDEX idx_inspection_anomaly_tenant ON inspection_anomaly (tenant_id);
CREATE INDEX idx_inspection_anomaly_status ON inspection_anomaly (status);
