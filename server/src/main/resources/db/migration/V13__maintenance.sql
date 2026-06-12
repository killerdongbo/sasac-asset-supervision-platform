CREATE TABLE maintenance_request (
    id                BIGINT PRIMARY KEY,
    asset_id          BIGINT        NOT NULL,
    provider_id       BIGINT,
    fault_description TEXT,
    priority          VARCHAR(16)   NOT NULL DEFAULT 'MEDIUM',
    source_type       VARCHAR(32)   NOT NULL DEFAULT 'MANUAL',
    source_anomaly_id BIGINT,
    status            VARCHAR(16)   NOT NULL DEFAULT 'PENDING',
    expected_date     DATE,
    tenant_id         BIGINT        NOT NULL DEFAULT 0,
    created_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted           INTEGER       DEFAULT 0
);

CREATE INDEX idx_maintenance_request_asset   ON maintenance_request (asset_id);
CREATE INDEX idx_maintenance_request_tenant  ON maintenance_request (tenant_id);
CREATE INDEX idx_maintenance_request_status  ON maintenance_request (status);
CREATE INDEX idx_maintenance_request_anomaly ON maintenance_request (source_anomaly_id);

CREATE TABLE maintenance_record (
    id                  BIGINT PRIMARY KEY,
    request_id          BIGINT        NOT NULL,
    process_description TEXT,
    result              VARCHAR(32)   NOT NULL,
    cost                DECIMAL(12,2),
    completion_time     TIMESTAMP,
    service_provider    VARCHAR(256),
    tenant_id           BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted             INTEGER       DEFAULT 0
);

CREATE INDEX idx_maintenance_record_request ON maintenance_record (request_id);
CREATE INDEX idx_maintenance_record_tenant  ON maintenance_record (tenant_id);
