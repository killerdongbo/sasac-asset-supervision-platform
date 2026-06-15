-- Asset label management: add label-related columns to asset table
ALTER TABLE asset ADD COLUMN IF NOT EXISTS label_status VARCHAR(20) DEFAULT 'UNPRINTED';
ALTER TABLE asset ADD COLUMN IF NOT EXISTS print_count INT DEFAULT 0;
ALTER TABLE asset ADD COLUMN IF NOT EXISTS qr_content VARCHAR(500);
ALTER TABLE asset ADD COLUMN IF NOT EXISTS last_print_time TIMESTAMP;

COMMENT ON COLUMN asset.label_status IS '标签状态: UNPRINTED/PRINTED/DAMAGED';
COMMENT ON COLUMN asset.print_count IS '打印次数';
COMMENT ON COLUMN asset.qr_content IS '二维码内容(JSON)';
COMMENT ON COLUMN asset.last_print_time IS '最后打印时间';

CREATE INDEX idx_asset_label_status ON asset (tenant_id, label_status);
