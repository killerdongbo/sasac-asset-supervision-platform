CREATE TABLE report (
    id BIGINT PRIMARY KEY,
    report_type VARCHAR(32) NOT NULL,
    period VARCHAR(16) NOT NULL,
    org_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    submit_status VARCHAR(16) DEFAULT 'DRAFT',
    version INTEGER DEFAULT 1,
    snapshot_data TEXT,
    reviewer_id VARCHAR(64),
    review_remark VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX idx_report_org ON report(org_id);
CREATE INDEX idx_report_type_period ON report(report_type, period);
