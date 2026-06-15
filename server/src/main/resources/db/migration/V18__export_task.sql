CREATE TABLE export_task (
    id            BIGINT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    export_type   VARCHAR(50)  NOT NULL,
    file_name     VARCHAR(200),
    file_path     VARCHAR(500),
    status        VARCHAR(20)  DEFAULT 'PENDING',
    params        JSONB,
    total_rows    INT,
    error_message TEXT,
    created_by    BIGINT,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP
);

CREATE INDEX idx_export_task_tenant ON export_task (tenant_id);
CREATE INDEX idx_export_task_status ON export_task (tenant_id, status);
CREATE INDEX idx_export_task_created ON export_task (created_at DESC);
