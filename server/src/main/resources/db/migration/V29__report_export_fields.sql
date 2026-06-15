-- V29: 为 export_task 表补充报表导出字段
-- 支持按报表期间查询导出任务

ALTER TABLE export_task ADD COLUMN IF NOT EXISTS report_period VARCHAR(20);
