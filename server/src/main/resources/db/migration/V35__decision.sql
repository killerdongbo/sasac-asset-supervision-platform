-- V35__decision.sql
-- 三重一大决策管理 - 决策事项、会议、决议、督办

CREATE TABLE decision_item (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    item_no VARCHAR(64) NOT NULL,
    item_type VARCHAR(32) NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT,
    amount DECIMAL(18,2),
    proposer_id BIGINT,
    proposer_name VARCHAR(64),
    department VARCHAR(128),
    urgency VARCHAR(16) DEFAULT 'NORMAL',
    approval_instance_id BIGINT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_decision_item_type ON decision_item(tenant_id, item_type);

CREATE TABLE decision_meeting (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    org_id BIGINT NOT NULL,
    meeting_no VARCHAR(64) NOT NULL,
    meeting_type VARCHAR(64),
    title VARCHAR(256) NOT NULL,
    agenda TEXT,
    scheduled_at TIMESTAMP NOT NULL,
    location VARCHAR(128),
    host VARCHAR(64),
    attendees TEXT,
    minutes TEXT,
    status VARCHAR(32) DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE decision_resolution (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    meeting_id BIGINT NOT NULL,
    item_id BIGINT,
    resolution_no VARCHAR(64) NOT NULL,
    title VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    vote_result TEXT,
    status VARCHAR(32) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

CREATE TABLE decision_supervision (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    resolution_id BIGINT NOT NULL,
    task_title VARCHAR(256) NOT NULL,
    description TEXT,
    assignee_id BIGINT,
    assignee_name VARCHAR(64),
    deadline DATE,
    completed_at TIMESTAMP,
    progress_note TEXT,
    status VARCHAR(32) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
CREATE INDEX idx_decision_supervision_status ON decision_supervision(tenant_id, status);
