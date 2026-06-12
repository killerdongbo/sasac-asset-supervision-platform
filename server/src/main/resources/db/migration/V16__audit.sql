CREATE TABLE operation_log (
    id            BIGINT PRIMARY KEY,
    operator_id   BIGINT        NOT NULL,
    operator_name VARCHAR(128)  NOT NULL,
    action        VARCHAR(32)   NOT NULL,
    target_type   VARCHAR(64)   NOT NULL,
    target_id     BIGINT        NOT NULL,
    before_data   TEXT,
    after_data    TEXT,
    ip            VARCHAR(64),
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_operation_log_target     ON operation_log (target_type, target_id);
CREATE INDEX idx_operation_log_tenant     ON operation_log (tenant_id);
CREATE INDEX idx_operation_log_action     ON operation_log (action);
CREATE INDEX idx_operation_log_operator   ON operation_log (operator_id);
CREATE INDEX idx_operation_log_created_at ON operation_log (created_at);
