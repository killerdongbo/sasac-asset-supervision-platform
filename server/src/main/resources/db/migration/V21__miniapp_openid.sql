-- P3: 微信小程序对接
-- 用户表增加openId字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS open_id VARCHAR(64);
CREATE INDEX IF NOT EXISTS idx_user_open_id ON sys_user(open_id);
