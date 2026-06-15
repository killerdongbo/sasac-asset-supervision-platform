CREATE TABLE asset_lifecycle_event (
    id            BIGINT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    asset_id      BIGINT       NOT NULL,
    event_type    VARCHAR(30)  NOT NULL,
    event_title   VARCHAR(200) NOT NULL,
    event_detail  JSONB,
    source_table  VARCHAR(50),
    source_id     BIGINT,
    operator_id   BIGINT,
    operator_name VARCHAR(50),
    event_time    TIMESTAMP    NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lifecycle_asset      ON asset_lifecycle_event (tenant_id, asset_id, event_time DESC);
CREATE INDEX idx_lifecycle_event_type ON asset_lifecycle_event (tenant_id, event_type);
CREATE INDEX idx_lifecycle_time       ON asset_lifecycle_event (event_time);
