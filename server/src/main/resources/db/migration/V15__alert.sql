CREATE TABLE alert_rule (
    id          BIGINT PRIMARY KEY,
    alert_type  VARCHAR(64)   NOT NULL,
    rule_name   VARCHAR(256)  NOT NULL,
    rule_config TEXT          NOT NULL,
    enabled     BOOLEAN       NOT NULL DEFAULT TRUE,
    tenant_id   BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted     INTEGER       DEFAULT 0
);

CREATE INDEX idx_alert_rule_tenant    ON alert_rule (tenant_id);
CREATE INDEX idx_alert_rule_type      ON alert_rule (alert_type);
CREATE INDEX idx_alert_rule_enabled   ON alert_rule (enabled);

CREATE TABLE alert_record (
    id          BIGINT PRIMARY KEY,
    rule_id     BIGINT        NOT NULL,
    alert_type  VARCHAR(64)   NOT NULL,
    title       VARCHAR(256)  NOT NULL,
    content     TEXT,
    alert_data  TEXT,
    status      VARCHAR(32)   NOT NULL DEFAULT 'ACTIVE',
    handled_by  BIGINT,
    handled_at  TIMESTAMP,
    tenant_id   BIGINT        NOT NULL DEFAULT 0,
    ref_id      BIGINT,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    deleted     INTEGER       DEFAULT 0
);

CREATE INDEX idx_alert_record_rule    ON alert_record (rule_id);
CREATE INDEX idx_alert_record_tenant  ON alert_record (tenant_id);
CREATE INDEX idx_alert_record_status  ON alert_record (status);
CREATE INDEX idx_alert_record_type    ON alert_record (alert_type);
CREATE INDEX idx_alert_record_ref     ON alert_record (ref_id);
