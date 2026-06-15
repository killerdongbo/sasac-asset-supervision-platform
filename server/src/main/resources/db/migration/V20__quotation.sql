-- 询价单
CREATE TABLE inquiry (
    id            BIGINT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    inquiry_no    VARCHAR(50)  NOT NULL,
    title         VARCHAR(200) NOT NULL,
    category      VARCHAR(50),
    specification VARCHAR(200),
    quantity      INT          NOT NULL DEFAULT 1,
    unit          VARCHAR(20),
    budget_amount DECIMAL(18,2),
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    deadline      TIMESTAMP,
    remark        TEXT,
    created_by    BIGINT,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted       SMALLINT     DEFAULT 0
);

CREATE UNIQUE INDEX uk_inquiry_no ON inquiry (tenant_id, inquiry_no);
CREATE INDEX idx_inquiry_status ON inquiry (tenant_id, status);

COMMENT ON TABLE inquiry IS '询价单';
COMMENT ON COLUMN inquiry.status IS 'DRAFT/OPEN/CLOSED/CANCELLED';

-- 报价记录
CREATE TABLE quotation (
    id            BIGINT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    inquiry_id    BIGINT       NOT NULL,
    supplier_id   BIGINT       NOT NULL,
    supplier_name VARCHAR(100) NOT NULL,
    unit_price    DECIMAL(18,4) NOT NULL,
    total_price   DECIMAL(18,2),
    tax_rate      DECIMAL(5,2) DEFAULT 0,
    delivery_days INT,
    warranty_months INT,
    remark        TEXT,
    is_selected   SMALLINT     DEFAULT 0,
    quoted_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted       SMALLINT     DEFAULT 0
);

CREATE INDEX idx_quotation_inquiry ON quotation (tenant_id, inquiry_id);
CREATE INDEX idx_quotation_supplier ON quotation (tenant_id, supplier_id);

COMMENT ON TABLE quotation IS '供应商报价记录';

-- 价格历史（用于趋势分析）
CREATE TABLE price_history (
    id            BIGINT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    category      VARCHAR(50)  NOT NULL,
    specification VARCHAR(200),
    supplier_id   BIGINT,
    supplier_name VARCHAR(100),
    unit_price    DECIMAL(18,4) NOT NULL,
    source_type   VARCHAR(30)  NOT NULL DEFAULT 'QUOTATION',
    source_id     BIGINT,
    record_date   DATE         NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_price_history_cat ON price_history (tenant_id, category, record_date);
CREATE INDEX idx_price_history_supplier ON price_history (tenant_id, supplier_id, record_date);

COMMENT ON TABLE price_history IS '价格历史记录（自动从报价同步）';
COMMENT ON COLUMN price_history.source_type IS 'QUOTATION/PURCHASE/MANUAL';
