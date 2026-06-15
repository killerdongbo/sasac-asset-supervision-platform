-- P10: 文件附件管理
CREATE TABLE IF NOT EXISTS sys_file (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    original_name VARCHAR(256) NOT NULL,
    storage_name VARCHAR(256) NOT NULL,
    bucket_name VARCHAR(64) DEFAULT 'sasac-assets',
    object_path VARCHAR(512) NOT NULL,
    file_size BIGINT DEFAULT 0,
    content_type VARCHAR(128),
    file_ext VARCHAR(16),
    md5 VARCHAR(64),
    biz_type VARCHAR(64),
    biz_id BIGINT,
    is_image INTEGER DEFAULT 0,
    thumb_path VARCHAR(512),
    upload_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_file_biz ON sys_file(biz_type, biz_id);
CREATE INDEX IF NOT EXISTS idx_file_md5 ON sys_file(md5);

COMMENT ON TABLE sys_file IS '文件附件表';
COMMENT ON COLUMN sys_file.is_image IS '是否为图片（用于前端预览）';
COMMENT ON COLUMN sys_file.thumb_path IS '缩略图路径';
