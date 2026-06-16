-- ============================================
-- 国资监管平台 Mock 数据
-- 执行: docker exec -i sasac-postgres psql -U sasac -d sasac_platform < mock_data.sql
-- ============================================

-- ============================================
-- 清理已有 mock 数据（按依赖顺序反向删除，保证幂等）
-- ============================================
DELETE FROM price_history WHERE id >= 100;
DELETE FROM quotation WHERE id >= 200;
DELETE FROM inquiry WHERE id >= 100;
DELETE FROM sys_notification WHERE id >= 100;
DELETE FROM export_task WHERE id >= 100;
DELETE FROM report WHERE id >= 100;
DELETE FROM approval_instance WHERE id >= 400;
DELETE FROM approval_add_sign WHERE id >= 100;
DELETE FROM approval_timeout_record WHERE id >= 100;
DELETE FROM approval_node WHERE id >= 300;
DELETE FROM approval_def WHERE id >= 100;
DELETE FROM purchase_acceptance WHERE id >= 200;
DELETE FROM purchase_request WHERE id >= 300;
DELETE FROM maintenance_record WHERE id >= 100;
DELETE FROM maintenance_request WHERE id >= 100;
DELETE FROM inventory_diff WHERE id >= 100;
DELETE FROM inventory_record WHERE id >= 200;
DELETE FROM inventory_task WHERE id >= 100;
DELETE FROM inspection_anomaly WHERE id >= 100;
DELETE FROM inspection_record WHERE id >= 200;
DELETE FROM inspection_task WHERE id >= 100;
DELETE FROM operation_log WHERE id >= 200;
DELETE FROM disposal WHERE id >= 200;
DELETE FROM depreciation WHERE id >= 300;
DELETE FROM asset_lifecycle_event WHERE id >= 200;
DELETE FROM asset_label WHERE id >= 200;
DELETE FROM asset_change_log WHERE id >= 100;
DELETE FROM asset_change WHERE id >= 100;
DELETE FROM asset WHERE id >= 200;
DELETE FROM asset_maintenance_provider WHERE id >= 100;
DELETE FROM asset_supplier WHERE id >= 100;
DELETE FROM asset_location WHERE id >= 100;
DELETE FROM sys_user_role WHERE id >= 100;
DELETE FROM sys_user WHERE id >= 100;
DELETE FROM sys_organization WHERE id >= 100;
DELETE FROM sys_tenant_config WHERE id >= 100;
DELETE FROM sys_tenant_usage WHERE id >= 100;

-- ============================================
-- 第1部分: 组织 sys_organization
-- ============================================
INSERT INTO sys_organization (id, parent_id, name, org_type, org_code, tenant_id, sort_order, status, created_at, updated_at, deleted) VALUES
(100, NULL, '湛江市国有资产监督管理委员会', 'SASAC', 'SASAC-ZJ', 1, 1, 1, NOW(), NOW(), 0),
(101, 100,  '湛江城市投资集团有限公司',      'GROUP',      'JITOU-ZJ', 1, 2, 1, NOW(), NOW(), 0),
(102, 100,  '湛江交通投资集团有限公司',      'GROUP',      'JTJT-ZJ',  1, 3, 1, NOW(), NOW(), 0),
(103, 101,  '湛江市政工程有限公司',           'ENTERPRISE', 'SZGC-ZJ',  1, 4, 1, NOW(), NOW(), 0),
(104, 101,  '湛江城市运营管理有限公司',       'ENTERPRISE', 'CSYY-ZJ',  1, 5, 1, NOW(), NOW(), 0),
(105, 102,  '湛江市公共交通有限公司',         'ENTERPRISE', 'GJJT-ZJ',  1, 6, 1, NOW(), NOW(), 0),
(106, 102,  '湛江港口运营管理有限公司',       'ENTERPRISE', 'GKYY-ZJ',  1, 7, 1, NOW(), NOW(), 0);

-- ============================================
-- 第2部分: 用户 sys_user (密码均为 123456)
-- ============================================
INSERT INTO sys_user (id, username, password, real_name, phone, tenant_id, org_id, role_code, status, created_at, updated_at, deleted) VALUES
(100, 'zhang_san', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张三', '13800000100', 1, 101, 'TENANT_ADMIN',        1, NOW(), NOW(), 0),
(101, 'li_si',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李四', '13800000101', 1, 103, 'ORG_MANAGER',         1, NOW(), NOW(), 0),
(102, 'wang_wu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王五', '13800000102', 1, 104, 'ENTERPRISE_OPERATOR', 1, NOW(), NOW(), 0),
(103, 'zhao_liu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵六', '13800000103', 1, 105, 'ENTERPRISE_OPERATOR', 1, NOW(), NOW(), 0),
(104, 'chen_qi',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '陈七', '13800000104', 1, 106, 'VIEWER',              1, NOW(), NOW(), 0);

-- 用户-角色关联
INSERT INTO sys_user_role (id, user_id, role_id, created_at, updated_at, deleted) VALUES
(100, 100, 2, NOW(), NOW(), 0),
(101, 101, 3, NOW(), NOW(), 0),
(102, 102, 4, NOW(), NOW(), 0),
(103, 103, 4, NOW(), NOW(), 0),
(104, 104, 5, NOW(), NOW(), 0);

-- ============================================
-- 第3部分: 位置 asset_location
-- ============================================
INSERT INTO asset_location (id, name, parent_id, address, tenant_id, sort_order, status, created_at, updated_at, deleted) VALUES
(100, '湛江市总部基地',   NULL, '湛江市赤坎区海滨大道88号',   1, 1, 1, NOW(), NOW(), 0),
(101, '湛江市政工程基地', 100,  '湛江市霞山区建设路12号',      1, 2, 1, NOW(), NOW(), 0),
(102, '湛江公交停车场',   100,  '湛江市赤坎区公交枢纽站',      1, 3, 1, NOW(), NOW(), 0),
(103, '湛江港区仓库',     100,  '湛江市霞山区港口路1号',       1, 4, 1, NOW(), NOW(), 0),
(104, '城投大厦',         100,  '湛江市开发区人民大道中45号',   1, 5, 1, NOW(), NOW(), 0),
(105, '人民大道商业区',   100,  '湛江市开发区人民大道',        1, 6, 1, NOW(), NOW(), 0);

-- ============================================
-- 第4部分: 供应商 asset_supplier
-- ============================================
INSERT INTO asset_supplier (id, name, contact, phone, address, business_scope, tenant_id, status, created_at, updated_at, deleted) VALUES
(100, '华为技术有限公司',       '华为对接人', '0755-28780808', '深圳市龙岗区坂田华为基地',  'ICT设备/服务器',      1, 1, NOW(), NOW(), 0),
(101, '联想集团',               '联想对接人', '0755-12345678', '深圳市福田区联想大厦',      '计算机/笔记本',       1, 1, NOW(), NOW(), 0),
(102, '广汽丰田汽车有限公司',   '丰田对接人', '020-12345678',  '广州市南沙区黄阁镇',        '车辆',                1, 1, NOW(), NOW(), 0),
(103, '三一重工股份有限公司',   '三一对接人', '0731-84031999', '长沙市经济技术开发区',      '工程设备',             1, 1, NOW(), NOW(), 0),
(104, '湛江振华建材有限公司',   '陈经理',     '0759-2223333',  '湛江市霞山区建材路88号',    '建筑材料',             1, 1, NOW(), NOW(), 0),
(105, '珠海东方办公家具有限公司','林经理',     '0756-8889999',  '珠海市香洲区工业园',        '办公家具',             1, 1, NOW(), NOW(), 0);

-- ============================================
-- 第5部分: 维保服务商 asset_maintenance_provider
-- ============================================
INSERT INTO asset_maintenance_provider (id, name, contact, phone, service_types, tenant_id, status, created_at, updated_at, deleted) VALUES
(100, '湛江机电设备维修公司', '黄工',  '0759-3334444', '机电设备,工程设备',        1, 1, NOW(), NOW(), 0),
(101, '湛江汽车修理厂',       '周厂长','0759-5556666', '车辆维修保养',              1, 1, NOW(), NOW(), 0),
(102, '湛江IT技术服务公司',   '刘经理','0759-7778888', '电子设备,网络设备',         1, 1, NOW(), NOW(), 0),
(103, '湛江建筑修缮工程公司', '吴经理','0759-9990000', '房屋建筑维修',              1, 1, NOW(), NOW(), 0);

-- ============================================
-- 第6部分: 资产 asset (15条, 覆盖6类别4状态12个月)
-- ============================================
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, depreciation_method, useful_life_months, residual_rate, use_status, use_department, custodian, location, address, purchase_date, registration_date, source_type, certificate_no, remark, label_status, print_count, created_at, updated_at, deleted) VALUES
(200, '湛江海滨大道储备地块',  'ZC-2025-001', 'LAND',         103, 1, '5000平方米',    '平方米', 5000, 50000000.00, 50000000.00, 0,            'STRAIGHT_LINE', 0,    0.05, 'IN_USE',   '工程部', '李四', '湛江市政工程基地', '湛江市海滨大道88号',          '2025-07-15', '2025-07-20', 'PURCHASE', '粤(2025)湛江市不动产权第0001号', '储备开发用地',                                                   'PRINTED', 1, '2025-07-20'::timestamp, '2025-07-20'::timestamp, 0),
(201, '湛江总部大厦A座',        'FC-2025-002',  'REAL_ESTATE', 101, 1, '15层框架结构',  '栋',     1,  35000000.00, 34331250.00, 668750.00,    'STRAIGHT_LINE', 480,  0.05, 'IN_USE',   '集团办公室','张三','城投大厦',       '湛江市开发区人民大道中45号',   '2025-08-01', '2025-08-10', 'PURCHASE', '粤(2025)湛江市不动产权第0002号', '集团总部办公楼',                                                   'PRINTED', 2, '2025-08-10'::timestamp, '2025-08-10'::timestamp, 0),
(202, '人民大道沿街商铺',        'FC-2025-003',  'REAL_ESTATE', 104, 1, '200平方米/间',  '间',     3,   8000000.00,  7858333.30, 141666.70,    'STRAIGHT_LINE', 480,  0.05, 'RENTED',   '运营部', '王五', '人民大道商业区',   '湛江市开发区人民大道',         '2025-09-10', '2025-09-18', 'PURCHASE', '粤(2025)湛江市不动产权第0003号', '对外出租商铺',                                                     'PRINTED', 1, '2025-09-18'::timestamp, '2025-09-18'::timestamp, 0),
(203, '数控加工中心',            'SB-2025-004',  'EQUIPMENT',   103, 1, 'CNC-850',       '台',     1,   1200000.00,  1111500.00,  88500.00,     'STRAIGHT_LINE', 120,  0.05, 'IN_USE',   '生产部', '李四', '湛江市政工程基地', '湛江市霞山区建设路12号',       '2025-10-05', '2025-10-12', 'PURCHASE', 'SB-FP-2025001', '高精度数控加工设备',                                              'PRINTED', 1, '2025-10-12'::timestamp, '2025-10-12'::timestamp, 0),
(204, '混凝土搅拌站',            'SB-2025-005',  'EQUIPMENT',   103, 1, 'HZS180',        '套',     1,   2500000.00,  2350000.00,  150000.00,    'STRAIGHT_LINE', 120,  0.05, 'IDLE',     '生产部', '李四', '湛江市政工程基地', '湛江市霞山区建设路12号',       '2025-11-20', '2025-11-28', 'PURCHASE', 'SB-FP-2025002', '闲置待维修，电机启动异常',                                         'PRINTED', 1, '2025-11-28'::timestamp, '2025-11-28'::timestamp, 0),
(205, '自动化装配流水线',        'SB-2026-001',  'EQUIPMENT',   104, 1, 'ZLX-2000',      '条',     1,   3800000.00,  3650000.00,  150000.00,    'STRAIGHT_LINE', 120,  0.05, 'IN_USE',   '运营部', '王五', '城投大厦',         '湛江市开发区人民大道中45号',   '2026-01-15', '2026-01-22', 'PURCHASE', 'SB-FP-2026001', '城市运营自动化产线',                                              'PRINTED', 1, '2026-01-22'::timestamp, '2026-01-22'::timestamp, 0),
(206, '丰田柯斯达中型客车',      'CL-2025-006',  'VEHICLE',     105, 1, '柯斯达 2025款', '辆',     1,    380000.00,   360000.00,   20000.00,     'STRAIGHT_LINE', 96,   0.05, 'IN_USE',   '车队管理部','赵六','湛江公交停车场',   '湛江市赤坎区公交枢纽站',       '2025-12-01', '2025-12-08', 'PURCHASE', 'CL-FP-2025001', '车牌: 粤G-K0001',                                                 'PRINTED', 2, '2025-12-08'::timestamp, '2025-12-08'::timestamp, 0),
(207, '纯电动公交车(10辆)',       'CL-2025-007',  'VEHICLE',     105, 1, 'BYD K9',        '辆',    10,  2800000.00,  2300000.00,  500000.00,    'STRAIGHT_LINE', 96,   0.05, 'DISPOSED', '车队管理部','赵六','湛江公交停车场',   '湛江市赤坎区公交枢纽站',       '2025-09-15', '2025-09-25', 'PURCHASE', 'CL-FP-2025002', '电池老化已报废处置',                                               'PRINTED', 1, '2025-09-25'::timestamp, '2025-09-25'::timestamp, 0),
(208, '大众帕萨特公务用车',      'CL-2026-002',  'VEHICLE',     101, 1, '帕萨特 2026款', '辆',     1,    240000.00,   235000.00,    5000.00,     'STRAIGHT_LINE', 96,   0.05, 'IN_USE',   '集团行政部','张三','城投大厦',       '湛江市开发区人民大道中45号',   '2026-02-20', '2026-02-28', 'PURCHASE', 'CL-FP-2026001', '车牌: 粤G-A8888',                                                 'PRINTED', 1, '2026-02-28'::timestamp, '2026-02-28'::timestamp, 0),
(209, '会议室办公桌椅套装',      'JJ-2026-003',  'FURNITURE',   101, 1, '实木会议桌+椅', '套',    50,    150000.00,   147500.00,    2500.00,     'STRAIGHT_LINE', 60,   0.05, 'IN_USE',   '行政部', '张三', '城投大厦',         '湛江市开发区人民大道中45号',   '2026-03-01', '2026-03-05', 'PURCHASE', 'JJ-FP-2026001', '集团会议室家具',                                                   'PRINTED', 1, '2026-03-05'::timestamp, '2026-03-05'::timestamp, 0),
(210, '定制员工工位',            'JJ-2026-004',  'FURNITURE',   106, 1, '1.5m×1.5m',     '套',   100,    280000.00,   276000.00,    4000.00,     'STRAIGHT_LINE', 60,   0.05, 'IDLE',     '行政部', '陈七', '湛江港区仓库',     '湛江市霞山区港口路1号',        '2026-04-10', '2026-04-15', 'PURCHASE', 'JJ-FP-2026002', '待分配工位，暂存仓库',                                             'PRINTED', 1, '2026-04-15'::timestamp, '2026-04-15'::timestamp, 0),
(211, '华为服务器集群',          'IT-2026-005',  'IT_EQUIPMENT', 101, 1, 'TaiShan 200',   '台',     5,    860000.00,   840000.00,   20000.00,     'STRAIGHT_LINE', 36,   0.05, 'IN_USE',   '信息技术部','张三','城投大厦',       '湛江市开发区人民大道中45号',   '2026-05-15', '2026-05-20', 'PURCHASE', 'IT-FP-2026001', '数据中心服务器集群',                                               'PRINTED', 1, '2026-05-20'::timestamp, '2026-05-20'::timestamp, 0),
(212, '台式计算机(100台)',        'IT-2025-008',  'IT_EQUIPMENT', 106, 1, '联想启天M4600',  '台',   100,    500000.00,   400000.00,  100000.00,    'STRAIGHT_LINE', 36,   0.05, 'RENTED',   '信息技术部','陈七','湛江港区仓库',     '湛江市霞山区港口路1号',        '2025-10-20', '2025-10-28', 'PURCHASE', 'IT-FP-2025002', '对外租赁给湛江港务局',                                             'PRINTED', 3, '2025-10-28'::timestamp, '2025-10-28'::timestamp, 0),
(213, '思科网络交换机(10台)',     'IT-2026-006',  'IT_EQUIPMENT', 104, 1, 'Catalyst 9300', '台',    10,    120000.00,   118000.00,    2000.00,     'STRAIGHT_LINE', 36,   0.05, 'IN_USE',   '信息技术部','王五','城投大厦',       '湛江市开发区人民大道中45号',   '2026-06-01', '2026-06-05', 'PURCHASE', 'IT-FP-2026002', '网络基础设施升级',                                                 'PRINTED', 1, '2026-06-05'::timestamp, '2026-06-05'::timestamp, 0),
(214, '惠普笔记本电脑(50台)',     'IT-2025-009',  'IT_EQUIPMENT', 105, 1, 'HP EliteBook',  '台',    50,    350000.00,   270000.00,   80000.00,     'STRAIGHT_LINE', 36,   0.05, 'DISPOSED', '信息技术部','赵六','湛江公交停车场',   '湛江市赤坎区公交枢纽站',       '2025-08-15', '2025-08-22', 'PURCHASE', 'IT-FP-2025001', '配置过低已批量报废',                                               'PRINTED', 2, '2025-08-22'::timestamp, '2025-08-22'::timestamp, 0);

-- ============================================
-- 第7部分: 资产标签 asset_label
-- ============================================
INSERT INTO asset_label (id, asset_id, label_code, barcode_data, print_count, last_print_time, tenant_id, created_at, updated_at, deleted) VALUES
(200, 200, 'ZC-2025-001-LBL', 'QR:ZC-2025-001', 1, '2025-07-20 10:00:00', 1, NOW(), NOW(), 0),
(201, 201, 'FC-2025-002-LBL', 'QR:FC-2025-002', 2, '2025-08-10 10:00:00', 1, NOW(), NOW(), 0),
(202, 202, 'FC-2025-003-LBL', 'QR:FC-2025-003', 1, '2025-09-18 10:00:00', 1, NOW(), NOW(), 0),
(203, 203, 'SB-2025-004-LBL', 'QR:SB-2025-004', 1, '2025-10-12 10:00:00', 1, NOW(), NOW(), 0),
(204, 204, 'SB-2025-005-LBL', 'QR:SB-2025-005', 1, '2025-11-28 10:00:00', 1, NOW(), NOW(), 0),
(205, 205, 'SB-2026-001-LBL', 'QR:SB-2026-001', 1, '2026-01-22 10:00:00', 1, NOW(), NOW(), 0),
(206, 206, 'CL-2025-006-LBL', 'QR:CL-2025-006', 2, '2025-12-08 10:00:00', 1, NOW(), NOW(), 0),
(207, 207, 'CL-2025-007-LBL', 'QR:CL-2025-007', 1, '2025-09-25 10:00:00', 1, NOW(), NOW(), 0),
(208, 208, 'CL-2026-002-LBL', 'QR:CL-2026-002', 1, '2026-02-28 10:00:00', 1, NOW(), NOW(), 0),
(209, 209, 'JJ-2026-003-LBL', 'QR:JJ-2026-003', 1, '2026-03-05 10:00:00', 1, NOW(), NOW(), 0),
(210, 210, 'JJ-2026-004-LBL', 'QR:JJ-2026-004', 1, '2026-04-15 10:00:00', 1, NOW(), NOW(), 0),
(211, 211, 'IT-2026-005-LBL', 'QR:IT-2026-005', 1, '2026-05-20 10:00:00', 1, NOW(), NOW(), 0),
(212, 212, 'IT-2025-008-LBL', 'QR:IT-2025-008', 3, '2025-10-28 10:00:00', 1, NOW(), NOW(), 0),
(213, 213, 'IT-2026-006-LBL', 'QR:IT-2026-006', 1, '2026-06-05 10:00:00', 1, NOW(), NOW(), 0),
(214, 214, 'IT-2025-009-LBL', 'QR:IT-2025-009', 2, '2025-08-22 10:00:00', 1, NOW(), NOW(), 0);

-- ============================================
-- 第8部分: 生命周期事件 asset_lifecycle_event
-- ============================================
INSERT INTO asset_lifecycle_event (id, tenant_id, asset_id, event_type, event_title, event_detail, source_table, source_id, operator_id, operator_name, event_time, created_at) VALUES
(200, 1, 200, 'CREATION',       '土地资产登记入账',       '{"value":50000000,"org":"湛江市政工程有限公司"}',                     'asset', 200, 101, '李四', '2025-07-20 10:00:00', NOW()),
(201, 1, 201, 'CREATION',       '总部大厦资产登记入账',   '{"value":35000000,"org":"湛江城市投资集团有限公司"}',                 'asset', 201, 100, '张三', '2025-08-10 10:00:00', NOW()),
(202, 1, 202, 'CREATION',       '商铺资产登记入账',       '{"value":8000000,"org":"湛江城市运营管理有限公司"}',                  'asset', 202, 102, '王五', '2025-09-18 10:00:00', NOW()),
(203, 1, 203, 'CREATION',       '数控加工中心登记入账',   '{"value":1200000,"org":"湛江市政工程有限公司"}',                     'asset', 203, 101, '李四', '2025-10-12 10:00:00', NOW()),
(204, 1, 204, 'CREATION',       '混凝土搅拌站登记入账',   '{"value":2500000,"org":"湛江市政工程有限公司"}',                     'asset', 204, 101, '李四', '2025-11-28 10:00:00', NOW()),
(205, 1, 205, 'CREATION',       '自动化流水线登记入账',   '{"value":3800000,"org":"湛江城市运营管理有限公司"}',                  'asset', 205, 102, '王五', '2026-01-22 10:00:00', NOW()),
(206, 1, 206, 'CREATION',       '中型客车登记入账',       '{"value":380000,"org":"湛江市公共交通有限公司"}',                    'asset', 206, 103, '赵六', '2025-12-08 10:00:00', NOW()),
(207, 1, 207, 'CREATION',       '电动公交车登记入账',     '{"value":2800000,"quantity":10,"org":"湛江市公共交通有限公司"}',     'asset', 207, 103, '赵六', '2025-09-25 10:00:00', NOW()),
(208, 1, 208, 'CREATION',       '公务用车登记入账',       '{"value":240000,"org":"湛江城市投资集团有限公司"}',                  'asset', 208, 100, '张三', '2026-02-28 10:00:00', NOW()),
(209, 1, 209, 'CREATION',       '办公桌椅登记入账',       '{"value":150000,"quantity":50,"org":"湛江城市投资集团有限公司"}',    'asset', 209, 100, '张三', '2026-03-05 10:00:00', NOW()),
(210, 1, 210, 'CREATION',       '定制工位登记入账',       '{"value":280000,"quantity":100,"org":"湛江港口运营管理有限公司"}',   'asset', 210, 104, '陈七', '2026-04-15 10:00:00', NOW()),
(211, 1, 211, 'CREATION',       '服务器集群登记入账',     '{"value":860000,"quantity":5,"org":"湛江城市投资集团有限公司"}',     'asset', 211, 100, '张三', '2026-05-20 10:00:00', NOW()),
(212, 1, 212, 'CREATION',       '台式计算机登记入账',     '{"value":500000,"quantity":100,"org":"湛江港口运营管理有限公司"}',   'asset', 212, 104, '陈七', '2025-10-28 10:00:00', NOW()),
(213, 1, 213, 'CREATION',       '网络交换机登记入账',     '{"value":120000,"quantity":10,"org":"湛江城市运营管理有限公司"}',    'asset', 213, 102, '王五', '2026-06-05 10:00:00', NOW()),
(214, 1, 214, 'CREATION',       '笔记本电脑登记入账',     '{"value":350000,"quantity":50,"org":"湛江市公共交通有限公司"}',      'asset', 214, 103, '赵六', '2025-08-22 10:00:00', NOW()),
(215, 1, 202, 'RENTAL',         '商铺出租给湛江商贸有限公司','{"rentalStart":"2026-01-01","rentalEnd":"2028-12-31"}',            'asset', 202, 102, '王五', '2026-01-01 09:00:00', NOW()),
(216, 1, 212, 'RENTAL',         '计算机出租给湛江港务局',  '{"rentalStart":"2026-01-01","rentalEnd":"2026-12-31"}',            'asset', 212, 104, '陈七', '2026-01-01 09:00:00', NOW()),
(217, 1, 207, 'DISPOSAL',       '公交车报废处置',          '{"disposalType":"SCRAPPED","bookValue":2300000,"disposalValue":1800000}', 'disposal', 200, 103, '赵六', '2026-06-01 10:00:00', NOW()),
(218, 1, 214, 'DISPOSAL',       '笔记本电脑报废处置',     '{"disposalType":"SCRAPPED","bookValue":270000,"disposalValue":150000}',   'disposal', 201, 103, '赵六', '2026-05-20 10:00:00', NOW()),
(219, 1, 204, 'STATUS_CHANGE',  '搅拌站转为闲置状态',     '{"fromStatus":"IN_USE","toStatus":"IDLE","reason":"电机故障停机"}',  'asset', 204, 101, '李四', '2026-03-01 09:00:00', NOW()),
(220, 1, 210, 'STATUS_CHANGE',  '工位转为闲置状态',       '{"fromStatus":"IN_USE","toStatus":"IDLE","reason":"待分配"}',        'asset', 210, 104, '陈七', '2026-05-01 09:00:00', NOW()),
(221, 1, 201, 'INSPECTION',     '总部大厦季度安全检查',   '{"taskName":"2026年第一季度资产巡检"}',                               'inspection_record', 201, 101, '李四', '2026-01-20 14:00:00', NOW()),
(222, 1, 203, 'INSPECTION',     '数控设备季度巡检',       '{"taskName":"2026年第一季度资产巡检"}',                               'inspection_record', 202, 101, '李四', '2026-01-18 10:00:00', NOW()),
(223, 1, 211, 'MAINTENANCE',    '服务器散热系统维护',     '{"requestId":100,"result":"FIXED","cost":5000}',                     'maintenance_record', 100, 102, '王五', '2026-06-11 11:00:00', NOW()),
(224, 1, 201, 'DEPRECIATION',   '总部大厦月折旧计提',     '{"amount":66875,"period":"2026-06","method":"STRAIGHT_LINE"}',       'depreciation', 300, NULL, NULL, '2026-06-30 23:55:00', NOW());

-- ============================================
-- 第9部分: 折旧记录 depreciation (为每个需折旧资产生成逐月记录)
-- ============================================
-- 总部大厦 (ID=201): 2025-08 ~ 2026-06, 每月69270.83
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(300, 201, 69270.83, '2025-08-31', 35000000.00, 34930729.17, '2025-08', NOW(), NOW(), 0),
(301, 201, 69270.83, '2025-09-30', 34930729.17, 34861458.34, '2025-09', NOW(), NOW(), 0),
(302, 201, 69270.83, '2025-10-31', 34861458.34, 34792187.51, '2025-10', NOW(), NOW(), 0),
(303, 201, 69270.83, '2025-11-30', 34792187.51, 34722916.68, '2025-11', NOW(), NOW(), 0),
(304, 201, 69270.83, '2025-12-31', 34722916.68, 34653645.85, '2025-12', NOW(), NOW(), 0),
(305, 201, 69270.83, '2026-01-31', 34653645.85, 34584375.02, '2026-01', NOW(), NOW(), 0),
(306, 201, 69270.83, '2026-02-28', 34584375.02, 34515104.19, '2026-02', NOW(), NOW(), 0),
(307, 201, 69270.83, '2026-03-31', 34515104.19, 34445833.36, '2026-03', NOW(), NOW(), 0),
(308, 201, 69270.83, '2026-04-30', 34445833.36, 34376562.53, '2026-04', NOW(), NOW(), 0),
(309, 201, 69270.83, '2026-05-31', 34376562.53, 34307291.70, '2026-05', NOW(), NOW(), 0),
(310, 201, 69270.83, '2026-06-30', 34307291.70, 34238020.87, '2026-06', NOW(), NOW(), 0);

-- 商铺 (ID=202): 2025-09 ~ 2026-06, 月折旧15833.33
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(320, 202, 15833.33, '2025-09-30', 8000000.00, 7984166.67, '2025-09', NOW(), NOW(), 0),
(321, 202, 15833.33, '2025-10-31', 7984166.67, 7968333.34, '2025-10', NOW(), NOW(), 0),
(322, 202, 15833.33, '2025-11-30', 7968333.34, 7952500.01, '2025-11', NOW(), NOW(), 0),
(323, 202, 15833.33, '2025-12-31', 7952500.01, 7936666.68, '2025-12', NOW(), NOW(), 0),
(324, 202, 15833.33, '2026-01-31', 7936666.68, 7920833.35, '2026-01', NOW(), NOW(), 0),
(325, 202, 15833.33, '2026-02-28', 7920833.35, 7905000.02, '2026-02', NOW(), NOW(), 0),
(326, 202, 15833.33, '2026-03-31', 7905000.02, 7889166.69, '2026-03', NOW(), NOW(), 0),
(327, 202, 15833.33, '2026-04-30', 7889166.69, 7873333.36, '2026-04', NOW(), NOW(), 0),
(328, 202, 15833.33, '2026-05-31', 7873333.36, 7857500.03, '2026-05', NOW(), NOW(), 0),
(329, 202, 15833.33, '2026-06-30', 7857500.03, 7841666.70, '2026-06', NOW(), NOW(), 0);

-- 数控加工中心 (ID=203): 2025-10 ~ 2026-06, 月折旧9500
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(330, 203, 9500.00, '2025-10-31', 1200000.00, 1190500.00, '2025-10', NOW(), NOW(), 0),
(331, 203, 9500.00, '2025-11-30', 1190500.00, 1181000.00, '2025-11', NOW(), NOW(), 0),
(332, 203, 9500.00, '2025-12-31', 1181000.00, 1171500.00, '2025-12', NOW(), NOW(), 0),
(333, 203, 9500.00, '2026-01-31', 1171500.00, 1162000.00, '2026-01', NOW(), NOW(), 0),
(334, 203, 9500.00, '2026-02-28', 1162000.00, 1152500.00, '2026-02', NOW(), NOW(), 0),
(335, 203, 9500.00, '2026-03-31', 1152500.00, 1143000.00, '2026-03', NOW(), NOW(), 0),
(336, 203, 9500.00, '2026-04-30', 1143000.00, 1133500.00, '2026-04', NOW(), NOW(), 0),
(337, 203, 9500.00, '2026-05-31', 1133500.00, 1124000.00, '2026-05', NOW(), NOW(), 0),
(338, 203, 9500.00, '2026-06-30', 1124000.00, 1114500.00, '2026-06', NOW(), NOW(), 0);

-- 搅拌站 (ID=204): 2025-11 ~ 2026-06, 月折旧19791.67
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(340, 204, 19791.67, '2025-12-01', 2500000.00, 2480208.33, '2025-12', NOW(), NOW(), 0),
(341, 204, 19791.67, '2026-01-01', 2480208.33, 2460416.66, '2026-01', NOW(), NOW(), 0),
(342, 204, 19791.67, '2026-02-01', 2460416.66, 2440624.99, '2026-02', NOW(), NOW(), 0),
(343, 204, 19791.67, '2026-03-01', 2440624.99, 2420833.32, '2026-03', NOW(), NOW(), 0),
(344, 204, 19791.67, '2026-04-01', 2420833.32, 2401041.65, '2026-04', NOW(), NOW(), 0),
(345, 204, 19791.67, '2026-05-01', 2401041.65, 2381249.98, '2026-05', NOW(), NOW(), 0),
(346, 204, 19791.67, '2026-06-01', 2381249.98, 2361458.31, '2026-06', NOW(), NOW(), 0);

-- 流水线 (ID=205): 2026-01 ~ 2026-06, 月折旧30083.33
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(350, 205, 30083.33, '2026-02-01', 3800000.00, 3769916.67, '2026-02', NOW(), NOW(), 0),
(351, 205, 30083.33, '2026-03-01', 3769916.67, 3739833.34, '2026-03', NOW(), NOW(), 0),
(352, 205, 30083.33, '2026-04-01', 3739833.34, 3709750.01, '2026-04', NOW(), NOW(), 0),
(353, 205, 30083.33, '2026-05-01', 3709750.01, 3679666.68, '2026-05', NOW(), NOW(), 0),
(354, 205, 30083.33, '2026-06-01', 3679666.68, 3649583.35, '2026-06', NOW(), NOW(), 0);

-- 中巴 (ID=206): 2025-12 ~ 2026-06, 月折旧3760.42
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(360, 206, 3760.42, '2026-01-01', 380000.00, 376239.58, '2026-01', NOW(), NOW(), 0),
(361, 206, 3760.42, '2026-02-01', 376239.58, 372479.16, '2026-02', NOW(), NOW(), 0),
(362, 206, 3760.42, '2026-03-01', 372479.16, 368718.74, '2026-03', NOW(), NOW(), 0),
(363, 206, 3760.42, '2026-04-01', 368718.74, 364958.32, '2026-04', NOW(), NOW(), 0),
(364, 206, 3760.42, '2026-05-01', 364958.32, 361197.90, '2026-05', NOW(), NOW(), 0),
(365, 206, 3760.42, '2026-06-01', 361197.90, 357437.48, '2026-06', NOW(), NOW(), 0);

-- 电动车 (ID=207): 2025-10 ~ 2026-06, 月折旧27708.33
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(370, 207, 27708.33, '2025-10-01', 2800000.00, 2772291.67, '2025-10', NOW(), NOW(), 0),
(371, 207, 27708.33, '2025-11-01', 2772291.67, 2744583.34, '2025-11', NOW(), NOW(), 0),
(372, 207, 27708.33, '2025-12-01', 2744583.34, 2716875.01, '2025-12', NOW(), NOW(), 0),
(373, 207, 27708.33, '2026-01-01', 2716875.01, 2689166.68, '2026-01', NOW(), NOW(), 0),
(374, 207, 27708.33, '2026-02-01', 2689166.68, 2661458.35, '2026-02', NOW(), NOW(), 0),
(375, 207, 27708.33, '2026-03-01', 2661458.35, 2633750.02, '2026-03', NOW(), NOW(), 0),
(376, 207, 27708.33, '2026-04-01', 2633750.02, 2606041.69, '2026-04', NOW(), NOW(), 0),
(377, 207, 27708.33, '2026-05-01', 2606041.69, 2578333.36, '2026-05', NOW(), NOW(), 0),
(378, 207, 27708.33, '2026-06-01', 2578333.36, 2550625.03, '2026-06', NOW(), NOW(), 0);

-- 帕萨特 (ID=208): 2026-03 ~ 2026-06, 月折旧2375
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(380, 208, 2375.00, '2026-03-01', 240000.00, 237625.00, '2026-03', NOW(), NOW(), 0),
(381, 208, 2375.00, '2026-04-01', 237625.00, 235250.00, '2026-04', NOW(), NOW(), 0),
(382, 208, 2375.00, '2026-05-01', 235250.00, 232875.00, '2026-05', NOW(), NOW(), 0),
(383, 208, 2375.00, '2026-06-01', 232875.00, 230500.00, '2026-06', NOW(), NOW(), 0);

-- 会议桌椅 (ID=209): 2026-03 ~ 2026-06, 月折旧2375
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(390, 209, 2375.00, '2026-03-31', 150000.00, 147625.00, '2026-03', NOW(), NOW(), 0),
(391, 209, 2375.00, '2026-04-30', 147625.00, 145250.00, '2026-04', NOW(), NOW(), 0),
(392, 209, 2375.00, '2026-05-31', 145250.00, 142875.00, '2026-05', NOW(), NOW(), 0),
(393, 209, 2375.00, '2026-06-30', 142875.00, 140500.00, '2026-06', NOW(), NOW(), 0);

-- 工位 (ID=210): 2026-04 ~ 2026-06, 月折旧4433.33
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(400, 210, 4433.33, '2026-05-01', 280000.00, 275566.67, '2026-05', NOW(), NOW(), 0),
(401, 210, 4433.33, '2026-06-01', 275566.67, 271133.34, '2026-06', NOW(), NOW(), 0);

-- 服务器 (ID=211): 2026-06, 月折旧22694.44
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(410, 211, 22694.44, '2026-06-01', 860000.00, 837305.56, '2026-06', NOW(), NOW(), 0);

-- 联想电脑 (ID=212): 2025-11 ~ 2026-06, 月折旧13194.44
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(420, 212, 13194.44, '2025-11-01', 500000.00, 486805.56, '2025-11', NOW(), NOW(), 0),
(421, 212, 13194.44, '2025-12-01', 486805.56, 473611.12, '2025-12', NOW(), NOW(), 0),
(422, 212, 13194.44, '2026-01-01', 473611.12, 460416.68, '2026-01', NOW(), NOW(), 0),
(423, 212, 13194.44, '2026-02-01', 460416.68, 447222.24, '2026-02', NOW(), NOW(), 0),
(424, 212, 13194.44, '2026-03-01', 447222.24, 434027.80, '2026-03', NOW(), NOW(), 0),
(425, 212, 13194.44, '2026-04-01', 434027.80, 420833.36, '2026-04', NOW(), NOW(), 0),
(426, 212, 13194.44, '2026-05-01', 420833.36, 407638.92, '2026-05', NOW(), NOW(), 0),
(427, 212, 13194.44, '2026-06-01', 407638.92, 394444.48, '2026-06', NOW(), NOW(), 0);

-- 交换机 (ID=213): 2026-06, 月折旧3166.67
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(430, 213, 3166.67, '2026-06-30', 120000.00, 116833.33, '2026-06', NOW(), NOW(), 0);

-- 笔记本 (ID=214): 2025-09 ~ 2026-06, 月折旧9236.11 (已处置)
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period, created_at, updated_at, deleted) VALUES
(440, 214, 9236.11, '2025-09-01', 350000.00, 340763.89, '2025-09', NOW(), NOW(), 0),
(441, 214, 9236.11, '2025-10-01', 340763.89, 331527.78, '2025-10', NOW(), NOW(), 0),
(442, 214, 9236.11, '2025-11-01', 331527.78, 322291.67, '2025-11', NOW(), NOW(), 0),
(443, 214, 9236.11, '2025-12-01', 322291.67, 313055.56, '2025-12', NOW(), NOW(), 0),
(444, 214, 9236.11, '2026-01-01', 313055.56, 303819.45, '2026-01', NOW(), NOW(), 0),
(445, 214, 9236.11, '2026-02-01', 303819.45, 294583.34, '2026-02', NOW(), NOW(), 0),
(446, 214, 9236.11, '2026-03-01', 294583.34, 285347.23, '2026-03', NOW(), NOW(), 0),
(447, 214, 9236.11, '2026-04-01', 285347.23, 276111.12, '2026-04', NOW(), NOW(), 0),
(448, 214, 9236.11, '2026-05-01', 276111.12, 266875.01, '2026-05', NOW(), NOW(), 0);

-- ============================================
-- 第10部分: 处置记录 disposal
-- ============================================
INSERT INTO disposal (id, asset_id, disposal_type, disposal_date, book_value, disposal_value, gain_loss, reason, remark, approval_id, created_at, updated_at, deleted) VALUES
(200, 207, 'SCRAP', '2026-06-01', 2300000.00, 1800000.00, -500000.00, '电池老化严重', '维修成本过高', '401', NOW(), NOW(), 0),
(201, 214, 'SCRAP', '2026-05-20', 270000.00,  150000.00,  -120000.00, '配置过低批量报废', '已无维修价值', '401', NOW(), NOW(), 0);

-- ============================================
-- 第11部分: 资产变更 asset_change
-- ============================================
INSERT INTO asset_change (id, asset_id, change_type, from_org_id, to_org_id, from_custodian, to_custodian, change_value, change_date, reason, remark, operator_id, created_at, updated_at, deleted) VALUES
(100, 212, 'STOCK_IN',   NULL, 106, NULL,  '陈七', 500000.00,  '2025-10-28', '采购入库',        '', 104, NOW(), NOW(), 0),
(101, 206, 'ASSIGNMENT', 101,  105, '张三', '赵六',        0,  '2025-12-08', '调拨至公交公司',   '', 100, NOW(), NOW(), 0),
(102, 202, 'RENTAL',     104,  104, '王五', '王五', 8000000.00,  '2026-01-01', '对外出租',         '', 102, NOW(), NOW(), 0),
(103, 208, 'STOCK_IN',   NULL, 101, NULL,  '张三', 240000.00,  '2026-02-28', '采购入库',        '', 100, NOW(), NOW(), 0);

-- ============================================
-- 第12部分: 资产变更日志 asset_change_log
-- ============================================
INSERT INTO asset_change_log (id, asset_id, change_field, before_value, after_value, operator_id, remark, created_at, updated_at, deleted) VALUES
(100, 204, 'use_status', 'IN_USE', 'IDLE',   101, '搅拌站电机故障停机',  '2026-03-01 09:00:00', NOW(), 0),
(101, 202, 'use_status', 'IN_USE', 'RENTED', 102, '商铺出租',            '2026-01-01 09:00:00', NOW(), 0),
(102, 207, 'use_status', 'IN_USE', 'DISPOSED', 103, '公交车报废处置',  '2026-06-01 10:00:00', NOW(), 0),
(103, 214, 'use_status', 'IN_USE', 'DISPOSED', 103, '笔记本批量报废',  '2026-05-20 10:00:00', NOW(), 0),
(104, 210, 'use_status', 'IN_USE', 'IDLE',   104, '工位转入闲置',    '2026-05-01 09:00:00', NOW(), 0),
(105, 201, 'location',   '湛江市总部基地', '城投大厦', 100, '搬迁至城投大厦', '2025-12-01 10:00:00', NOW(), 0),
(106, 206, 'custodian',  '张三', '赵六',    100, '调拨交接',        '2025-12-08 10:00:00', NOW(), 0),
(107, 212, 'use_status', 'IN_USE', 'RENTED', 104, '计算机出租',      '2026-01-01 09:00:00', NOW(), 0);

-- ============================================
-- 第13部分: 操作日志 operation_log
-- ============================================
INSERT INTO operation_log (id, operator_id, operator_name, action, target_type, target_id, before_data, after_data, ip, tenant_id, created_at) VALUES
(200, 101, '李四', 'CREATE', 'Asset', 200, NULL, '{"name":"湛江海滨大道储备地块"}', '192.168.1.101', 1, '2025-07-20 10:00:00'),
(201, 100, '张三', 'CREATE', 'Asset', 201, NULL, '{"name":"湛江总部大厦A座"}', '192.168.1.100', 1, '2025-08-10 10:00:00'),
(202, 103, '赵六', 'CREATE', 'Asset', 214, NULL, '{"name":"惠普笔记本电脑", "quantity":50}', '192.168.1.103', 1, '2025-08-22 10:00:00'),
(203, 102, '王五', 'CREATE', 'Asset', 202, NULL, '{"name":"人民大道沿街商铺"}', '192.168.1.102', 1, '2025-09-18 10:00:00'),
(204, 101, '李四', 'UPDATE', 'Asset', 204, '{"use_status":"IN_USE"}', '{"use_status":"IDLE"}', '192.168.1.101', 1, '2026-03-01 09:00:00'),
(205, 102, '王五', 'UPDATE', 'Asset', 202, '{"use_status":"IN_USE"}', '{"use_status":"RENTED"}', '192.168.1.102', 1, '2026-01-01 09:00:00'),
(206, 100, '张三', 'LOGIN', 'User', 100, NULL, '{"ip":"192.168.1.100"}', '192.168.1.100', 1, '2026-06-14 08:30:00'),
(207, 101, '李四', 'LOGIN', 'User', 101, NULL, '{"ip":"192.168.1.101"}', '192.168.1.101', 1, '2026-06-13 09:00:00'),
(208, 103, '赵六', 'CREATE', 'InspectionTask', 100, NULL, '{"taskName":"2026年第一季度资产巡检"}', '192.168.1.103', 1, '2026-01-10 10:00:00'),
(209, 102, '王五', 'CREATE', 'InventoryTask', 100, NULL, '{"taskName":"2025年度资产盘点"}', '192.168.1.102', 1, '2025-12-20 10:00:00'),
(210, 100, '张三', 'APPROVE', 'ApprovalInstance', 402, '{"status":"PENDING"}', '{"status":"APPROVED"}', '192.168.1.100', 1, '2026-04-05 14:00:00'),
(211, 103, '赵六', 'DELETE', 'Asset', 207, '{"status":"DISPOSED"}', NULL, '192.168.1.103', 1, '2026-06-01 10:30:00'),
(212, 102, '王五', 'SUBMIT', 'PurchaseRequest', 300, NULL, '{"assetName":"华为服务器"}', '192.168.1.102', 1, '2026-05-10 09:00:00'),
(213, 100, '张三', 'UPDATE', 'User', 103, '{"status":1}', '{"status":1}', '192.168.1.100', 1, '2026-03-15 15:00:00'),
(214, 101, '李四', 'CREATE', 'MaintenanceRequest', 100, NULL, '{"asset_id":211,"fault":"散热异常"}', '192.168.1.101', 1, '2026-06-10 15:00:00');

-- ============================================
-- ============================================
-- 第14部分: 审批定义 approval_def (6个流程)
-- ============================================
INSERT INTO approval_def (id, def_name, biz_type, status, description, tenant_id, created_at, updated_at, deleted) VALUES
(100, '资产采购审批流程', 'PURCHASE', 'ACTIVE', '资产采购审批，>10万需上级审批', 1, NOW(), NOW(), 0),
(101, '资产处置审批流程', 'DISPOSAL', 'ACTIVE', '资产报废/出售/核销审批流程', 1, NOW(), NOW(), 0),
(102, '资产调拨审批流程', 'TRANSFER', 'ACTIVE', '资产跨组织调拨审批流程', 1, NOW(), NOW(), 0),
(103, '盘点差异审批流程', 'INVENTORY_DIFF', 'ACTIVE', '盘点差异处理审批流程', 1, NOW(), NOW(), 0),
(104, '维保维修审批流程', 'MAINTENANCE', 'ACTIVE', '资产维保维修申请审批流程', 1, NOW(), NOW(), 0),
(105, '监管上报审批流程', 'REPORT', 'ACTIVE', '19类国资报表上报审核流程', 1, NOW(), NOW(), 0);

-- ============================================
-- 审批节点 approval_node (每个流程2步, 共12个)
-- ============================================
INSERT INTO approval_node (id, def_id, node_order, approver_role, condition_type, condition_value, approve_mode, can_reject, timeout_hours, allow_add_sign, timeout_action, escalate_role, created_at, updated_at, deleted) VALUES
-- 采购审批: 企业操作员 → (金额>10万时)集团审核员
(300, 100, 1, 'ENTERPRISE_OPERATOR', NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'ORG_MANAGER',  NOW(), NOW(), 0),
(301, 100, 2, 'ORG_MANAGER',        'AMOUNT_GT', '100000', 'SINGLE', TRUE,  72, TRUE,  'ESCALATE', 'TENANT_ADMIN', NOW(), NOW(), 0),
-- 处置审批: 集团审核员 → 系统管理员
(302, 101, 1, 'ORG_MANAGER',        NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'TENANT_ADMIN',  NOW(), NOW(), 0),
(303, 101, 2, 'TENANT_ADMIN',       NULL,        NULL,    'SINGLE', TRUE,  72, TRUE,  'ESCALATE', 'SYSTEM_ADMIN',  NOW(), NOW(), 0),
-- 调拨审批: 企业操作员 → 集团审核员
(304, 102, 1, 'ENTERPRISE_OPERATOR', NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'ORG_MANAGER',  NOW(), NOW(), 0),
(305, 102, 2, 'ORG_MANAGER',        NULL,        NULL,    'SINGLE', TRUE,  72, TRUE,  'ESCALATE', 'TENANT_ADMIN', NOW(), NOW(), 0),
-- 盘点差异审批: 企业操作员 → 集团审核员
(306, 103, 1, 'ENTERPRISE_OPERATOR', NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'ORG_MANAGER',  NOW(), NOW(), 0),
(307, 103, 2, 'ORG_MANAGER',        NULL,        NULL,    'SINGLE', TRUE,  72, TRUE,  'ESCALATE', 'TENANT_ADMIN', NOW(), NOW(), 0),
-- 维保审批: 企业操作员 → 集团审核员
(308, 104, 1, 'ENTERPRISE_OPERATOR', NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'ORG_MANAGER',  NOW(), NOW(), 0),
(309, 104, 2, 'ORG_MANAGER',        NULL,        NULL,    'SINGLE', TRUE,  72, TRUE,  'ESCALATE', 'TENANT_ADMIN', NOW(), NOW(), 0),
-- 监管上报: 集团审核员 → 国资委监管员
(310, 105, 1, 'ORG_MANAGER',        NULL,        NULL,    'SINGLE', TRUE,  48, TRUE,  'ESCALATE', 'TENANT_ADMIN',  NOW(), NOW(), 0),
(311, 105, 2, 'SASAC_SUPERVISOR',   NULL,        NULL,    'SINGLE', TRUE,  120, TRUE, 'ESCALATE', 'SYSTEM_ADMIN',  NOW(), NOW(), 0);

-- ============================================
-- 第15部分: 审批实例 approval_instance (30条)
-- 设计:
--   待我审批(PENDING) = instances 400-409 (10条, 4条submitter=1 + 6条其他提交人)
--   我发起的(submitter=1) = instances 400-403,410-419 (14条, 4条PENDING + 10条已完成)
--   审批历史(submitter=1 & !PENDING) = instances 410-419 (10条已完成)
-- ============================================
INSERT INTO approval_instance (id, def_id, biz_id, biz_type, status, current_node, node_results, submitter_id, tenant_id, created_at, updated_at, deleted) VALUES
-- ── 待我审批 PENDING (10条: id=400-409) ──
(400, 100, 300, 'PURCHASE',       'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      1,   1, '2026-06-15 09:00:00', NOW(), 0),
(401, 100, 301, 'PURCHASE',       'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      1,   1, '2026-06-14 14:30:00', NOW(), 0),
(402, 101, 201, 'DISPOSAL',       'PENDING',  2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"PENDING"}]',       1,   1, '2026-06-13 11:00:00', NOW(), 0),
(403, 102, 202, 'TRANSFER',       'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      1,   1, '2026-06-12 08:30:00', NOW(), 0),
(404, 103, 400, 'INVENTORY_DIFF', 'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      102, 1, '2026-06-15 10:00:00', NOW(), 0),
(405, 104, 500, 'MAINTENANCE',    'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      102, 1, '2026-06-14 16:00:00', NOW(), 0),
(406, 105, 601, 'REPORT',         'PENDING',  2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"PENDING"}]',       103, 1, '2026-06-13 09:00:00', NOW(), 0),
(407, 100, 302, 'PURCHASE',       'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      103, 1, '2026-06-11 15:00:00', NOW(), 0),
(408, 104, 501, 'MAINTENANCE',    'PENDING',  2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"PENDING"}]',       104, 1, '2026-06-10 13:00:00', NOW(), 0),
(409, 101, 203, 'DISPOSAL',       'PENDING',  1, '[{"node":1,"status":"PENDING"}]',                                      104, 1, '2026-06-09 10:00:00', NOW(), 0),
-- ── 我发起的已完成 (10条: id=410-419, submitter=1) ──
(410, 100, 303, 'PURCHASE',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-08 09:00:00', NOW(), 0),
(411, 101, 204, 'DISPOSAL',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-07 11:00:00', NOW(), 0),
(412, 102, 205, 'TRANSFER',       'REJECTED', 2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"REJECTED"}]',      1,   1, '2026-06-06 14:00:00', NOW(), 0),
(413, 103, 401, 'INVENTORY_DIFF', 'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-05 10:00:00', NOW(), 0),
(414, 104, 502, 'MAINTENANCE',    'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-04 16:00:00', NOW(), 0),
(415, 105, 602, 'REPORT',         'REJECTED', 1, '[{"node":1,"status":"REJECTED"}]',                                     1,   1, '2026-06-03 08:00:00', NOW(), 0),
(416, 100, 304, 'PURCHASE',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-02 09:30:00', NOW(), 0),
(417, 101, 206, 'DISPOSAL',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-06-01 13:00:00', NOW(), 0),
(418, 102, 207, 'TRANSFER',       'REJECTED', 1, '[{"node":1,"status":"REJECTED"}]',                                     1,   1, '2026-05-28 15:00:00', NOW(), 0),
(419, 104, 503, 'MAINTENANCE',    'CANCELLED',2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"CANCELLED"}]',      1,   1, '2026-05-25 11:00:00', NOW(), 0),
-- ── 其他已完成的补充(10条: id=420-429, 丰富审批历史) ──
(420, 100, 305, 'PURCHASE',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-05-20 09:00:00', NOW(), 0),
(421, 105, 603, 'REPORT',         'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      1,   1, '2026-05-18 10:00:00', NOW(), 0),
(422, 103, 402, 'INVENTORY_DIFF', 'REJECTED', 2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"REJECTED"}]',      1,   1, '2026-05-15 14:00:00', NOW(), 0),
(423, 102, 208, 'TRANSFER',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      102, 1, '2026-06-08 09:00:00', NOW(), 0),
(424, 104, 504, 'MAINTENANCE',    'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      102, 1, '2026-06-02 16:00:00', NOW(), 0),
(425, 101, 207, 'DISPOSAL',       'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      103, 1, '2026-05-20 10:00:00', NOW(), 0),
(426, 100, 306, 'PURCHASE',       'REJECTED', 1, '[{"node":1,"status":"REJECTED"}]',                                     103, 1, '2026-05-10 09:00:00', NOW(), 0),
(427, 105, 604, 'REPORT',         'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      103, 1, '2026-04-25 11:00:00', NOW(), 0),
(428, 102, 209, 'TRANSFER',       'CANCELLED',2, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"CANCELLED"}]',      104, 1, '2026-04-15 15:00:00', NOW(), 0),
(429, 103, 403, 'INVENTORY_DIFF', 'APPROVED', 3, '[{"node":1,"status":"APPROVED"},{"node":2,"status":"APPROVED"}]',      104, 1, '2026-04-10 08:00:00', NOW(), 0);

-- ============================================
-- 第16部分: 采购请求 purchase_request + 验收 purchase_acceptance
-- ============================================
INSERT INTO purchase_request (id, asset_name, quantity, budget, supplier_id, org_id, tenant_id, request_reason, status, approval_instance_id, remark, created_at, updated_at, deleted) VALUES
(300, '华为服务器(5台)',    5,  500000.00, 100, 106, 1, '数据中心扩容需求',               'PENDING',  NULL, '', '2026-05-10 09:00:00', NOW(), 0),
(301, '办公台式电脑(50台)',  50, 300000.00, 101, 105, 1, '员工办公电脑更新换代',            'APPROVED', '402', '', '2026-04-01 09:00:00', NOW(), 0),
(302, '办公打印机(10台)',    10,  50000.00, 105, 104, 1, '各部门打印需求，现有设备老化严重', 'ACCEPTED',  NULL, '', '2026-03-15 09:00:00', NOW(), 0);

INSERT INTO purchase_acceptance (id, request_id, acceptance_result, actual_quantity, actual_amount, check_remark, asset_id, tenant_id, created_at, updated_at, deleted) VALUES
(200, 301, 'PASS', 50, 285000.00, '验收合格，全部到货',   212, 1, '2026-04-10 14:00:00', NOW(), 0),
(201, 302, 'PASS', 8,  42000.00,  '部分到货，验收合格',  NULL, 1, '2026-03-25 14:00:00', NOW(), 0);

-- ============================================
-- 第17部分: 巡检 inspection_task + record + anomaly
-- ============================================
INSERT INTO inspection_task (id, task_name, assignee_id, asset_scope, start_date, end_date, status, total_count, completed_count, tenant_id, created_at, updated_at, deleted) VALUES
(100, '2026年第一季度资产巡检', 101, '["200","201","203","204","206","212"]', '2026-01-15', '2026-01-30', 'COMPLETED',   6, 6, 1, NOW(), NOW(), 0),
(101, '2026年第二季度资产巡检', 102, '["205","208","213","202"]',              '2026-04-15', '2026-04-30', 'IN_PROGRESS', 4, 2, 1, NOW(), NOW(), 0);

INSERT INTO inspection_record (id, task_id, asset_id, is_normal, actual_location, actual_status, anomaly_type, description, inspector_id, tenant_id, created_at, updated_at, deleted) VALUES
(200, 100, 200, TRUE,  '湛江市政工程基地', 'IN_USE',  NULL,              '', 101, 1, '2026-01-18 10:00:00', NOW(), 0),
(201, 100, 201, TRUE,  '城投大厦',         'IN_USE',  NULL,              '', 101, 1, '2026-01-20 14:00:00', NOW(), 0),
(202, 100, 203, TRUE,  '湛江市政工程基地', 'IN_USE',  NULL,              '', 101, 1, '2026-01-18 10:00:00', NOW(), 0),
(203, 100, 204, FALSE, '湛江市政工程基地', 'IDLE',    'STATUS_ABNORMAL', '搅拌站闲置超过3个月，存在锈蚀状况', 101, 1, '2026-01-22 10:00:00', NOW(), 0),
(204, 100, 206, TRUE,  '湛江公交停车场',   'IN_USE',  NULL,              '', 101, 1, '2026-01-25 10:00:00', NOW(), 0),
(205, 100, 212, FALSE, '湛江港区仓库',     'RENTED',  'LOCATION_ANOMALY','部分计算机实际位置与登记位置不符',  101, 1, '2026-01-28 10:00:00', NOW(), 0);

INSERT INTO inspection_anomaly (id, task_id, record_id, asset_id, anomaly_type, description, resolution, status, maintenance_request_id, tenant_id, created_at, updated_at, deleted) VALUES
(100, 100, 203, 204, 'STATUS_ABNORMAL', '搅拌站闲置超3个月存在锈蚀', 'TRANSFER_TO_MAINTENANCE', 'OPEN',     '101', 1, NOW(), NOW(), 0),
(101, 100, 205, 212, 'LOCATION_ANOMALY', '部分计算机位置与登记不符', 'RECTIFY',                 'RESOLVED', NULL,  1, NOW(), NOW(), 0);

-- ============================================
-- 第18部分: 盘点 inventory_task + record + diff
-- ============================================
INSERT INTO inventory_task (id, task_name, assignee_id, scope_type, scope_value, start_date, end_date, status, total_count, completed_count, diff_count, tenant_id, created_at, updated_at, deleted) VALUES
(100, '2025年度资产盘点', 102, 'ORG', '101', '2025-12-20', '2025-12-30', 'COMPLETED', 4, 4, 1, 1, NOW(), NOW(), 0),
(101, '2026年中资产盘点', 102, 'ORG', '103', '2026-06-01', '2026-06-15', 'PENDING',   3, 0, 0, 1, NOW(), NOW(), 0);

INSERT INTO inventory_record (id, task_id, asset_id, book_name, book_location, book_status, is_exists, actual_location, actual_status, remark, operator_id, tenant_id, created_at, updated_at, deleted) VALUES
(200, 100, 201, '湛江总部大厦A座',     '城投大厦',    'IN_USE', TRUE,  '城投大厦',      'IN_USE', '', 102, 1, '2025-12-22 10:00:00', NOW(), 0),
(201, 100, 209, '会议室办公桌椅套装',   '城投大厦',    'IN_USE', TRUE,  '城投大厦',      'IN_USE', '', 102, 1, '2025-12-22 10:00:00', NOW(), 0),
(202, 100, 208, '大众帕萨特公务用车',   '城投大厦',    'IN_USE', TRUE,  '城投大厦',      'IN_USE', '', 102, 1, '2025-12-23 10:00:00', NOW(), 0),
(203, 100, 211, '华为服务器集群',       '城投大厦',    'IN_USE', FALSE, NULL,            NULL,     '盘亏 - 服务器实际未找到', 102, 1, '2025-12-23 10:00:00', NOW(), 0);

INSERT INTO inventory_diff (id, task_id, record_id, asset_id, diff_type, book_value, actual_value, description, status, approval_instance_id, tenant_id, created_at, updated_at, deleted) VALUES
(100, 100, 203, 211, 'SHORTAGE', '华为服务器集群(城投大厦)', '未找到实物', '盘亏 - 服务器集群实物缺失', 'OPEN', NULL, 1, NOW(), NOW(), 0);

-- ============================================
-- 第19部分: 维保 maintenance_request + record
-- ============================================
INSERT INTO maintenance_request (id, asset_id, provider_id, fault_description, priority, source_type, source_anomaly_id, status, expected_date, tenant_id, created_at, updated_at, deleted) VALUES
(100, 211, 102, '服务器集群散热风扇异响，CPU温度长期过高（>85度）',   'HIGH',   'MANUAL',      NULL, 'COMPLETED', '2026-06-15', 1, '2026-06-10 09:00:00', NOW(), 0),
(101, 204, 100, '搅拌站电机启动异常，控制系统报错代码E-045',          'MEDIUM', 'FROM_INSPECTION', 100, 'PENDING',   '2026-06-20', 1, '2026-05-20 09:00:00', NOW(), 0),
(102, 201, 103, '总部大厦5楼空调管道漏水，天花板严重潮湿',             'LOW',    'MANUAL',      NULL, 'PENDING',   '2026-07-01', 1, '2026-06-01 09:00:00', NOW(), 0);

INSERT INTO maintenance_record (id, request_id, process_description, result, cost, completion_time, service_provider, tenant_id, created_at, updated_at, deleted) VALUES
(100, 100, '更换散热风扇，清理灰尘，重新涂抹导热硅脂', 'FIXED', 5000.00, '2026-06-11 16:00:00', '湛江IT技术服务公司', 1, NOW(), NOW(), 0);

-- ============================================
-- 第20部分: 预警 alert_rule + alert_record
-- ============================================
INSERT INTO alert_rule (id, alert_type, rule_name, rule_config, enabled, tenant_id, created_at, updated_at, deleted) VALUES
(100, 'DEPRECIATION', '资产折旧率过高预警', '{"threshold":0.8,"type":"PERCENT"}',   TRUE, 1, NOW(), NOW(), 0),
(101, 'IDLE_ASSET',   '资产闲置超期预警',   '{"days":180,"type":"DURATION"}',        TRUE, 1, NOW(), NOW(), 0);

INSERT INTO alert_record (id, rule_id, alert_type, title, content, alert_data, status, handled_by, handled_at, tenant_id, ref_id, created_at, updated_at, deleted) VALUES
(200, 100, 'DEPRECIATION', '电动车组折旧率已达80%，建议处置',   '纯电动公交车(10辆)已折旧至原值的20%，建议启动报废处置流程',     '{"assetId":207,"depreciationRate":0.8}', 'ACTIVE',     NULL, NULL,         1, 207, '2026-05-01 09:00:00', NOW(), 0),
(201, 101, 'IDLE_ASSET',   '混凝土搅拌站闲置已超180天',        '搅拌站自2025年11月购入后停机闲置，已超180天预警阈值',           '{"assetId":204,"idleDays":195}',        'ACTIVE',     NULL, NULL,         1, 204, '2026-06-01 09:00:00', NOW(), 0),
(202, 100, 'DEPRECIATION', '笔记本电脑组折旧率超过70%',        '惠普笔记本电脑(50台)折旧率超过70%，建议关注处置时机',           '{"assetId":214,"depreciationRate":0.77}', 'ACTIVE',   NULL, NULL,         1, 214, '2026-04-01 09:00:00', NOW(), 0),
(203, 101, 'IDLE_ASSET',   '定制工位闲置已超过30天',           '港口运营公司100套定制工位自入库后闲置，需加快分配进度',         '{"assetId":210,"idleDays":45}',          'ACTIVE',     NULL, NULL,         1, 210, '2026-06-01 09:00:00', NOW(), 0),
(204, 100, 'DEPRECIATION', '台式计算机折旧率正常（已处理）',   '联想台式计算机(100台)折旧监控已处理',                           '{"assetId":212,"depreciationRate":0.2}',  'RESOLVED',  100,  '2026-01-15 14:00:00', 1, 212, '2026-01-01 09:00:00', NOW(), 0);

-- ============================================
-- 第21部分: 报表 report
-- ============================================
INSERT INTO report (id, report_type, period, org_id, tenant_id, submit_status, version, snapshot_data, reviewer_id, review_remark, created_at, updated_at, deleted) VALUES
(100, 'ASSET_SUMMARY',      '2026-Q1', 101, 1, 'SUBMITTED', 1, '{"totalAssets":15,"totalOriginalValue":106490000}',    100, '审核通过', '2026-04-01 10:00:00', NOW(), 0),
(101, 'DEPRECIATION',       '2026-05', 101, 1, 'DRAFT',     1, '{"totalDepreciation":235800}',                         NULL, NULL,       '2026-06-01 10:00:00', NOW(), 0),
(102, 'DISPOSAL',           '2026-Q1', 101, 1, 'SUBMITTED', 1, '{"disposalCount":2,"bookValueTotal":2570000}',         100, '资料齐全', '2026-04-01 10:00:00', NOW(), 0);

-- ============================================
-- 第22部分: 导出任务 export_task
-- ============================================
INSERT INTO export_task (id, tenant_id, export_type, file_name, file_path, status, params, total_rows, error_message, created_by, created_at, completed_at) VALUES
(100, 1, 'ASSET_EXPORT',        '资产清单_20260515.xlsx',  '/exports/asset_20260515.xlsx',  'COMPLETED', '{"filters":{}}', 15, NULL,                    103, '2026-05-15 10:00:00', '2026-05-15 10:00:05'),
(101, 1, 'DEPRECIATION_EXPORT', '折旧明细_20260601.xlsx',  '/exports/depr_20260601.xlsx',   'FAILED',    '{"filters":{}}', NULL, 'MinIO 连接超时',        102, '2026-06-01 10:00:00', '2026-06-01 10:00:03');

-- ============================================
-- 第23部分: 询价报价 inquiry + quotation + price_history
-- ============================================
INSERT INTO inquiry (id, tenant_id, inquiry_no, title, category, specification, quantity, unit, budget_amount, status, deadline, remark, created_by, created_at, updated_at, deleted) VALUES
(100, 1, 'INQ-2026-001', '华为服务器采购询价',      'IT_EQUIPMENT', 'TaiShan 200服务器',  5,   '台', 500000.00, 'CLOSED', '2026-05-20 18:00:00', '数据中心扩容', 102, '2026-05-01 09:00:00', NOW(), 0),
(101, 1, 'INQ-2026-002', '定制办公工位采购询价',    'FURNITURE',    '1.5m×1.5m工位',    100,  '套', 300000.00, 'OPEN',   '2026-06-30 18:00:00', '新员工办公区', 100, '2026-06-01 09:00:00', NOW(), 0);

INSERT INTO quotation (id, tenant_id, inquiry_id, supplier_id, supplier_name, unit_price, total_price, tax_rate, delivery_days, warranty_months, remark, is_selected, quoted_at, created_at, updated_at, deleted) VALUES
(200, 1, 100, 100, '华为技术有限公司',      86000.0000, 430000.00, 13.00, 30, 36, '含安装调试服务',        1, '2026-05-12 10:00:00', NOW(), NOW(), 0),
(201, 1, 100, 101, '联想集团',              92000.0000, 460000.00, 13.00, 45, 36, '含3年上门服务',        0, '2026-05-13 10:00:00', NOW(), NOW(), 0),
(202, 1, 101, 105, '珠海东方办公家具有限公司',2550.0000, 255000.00, 13.00, 15, 24, '含安装',               1, '2026-06-10 10:00:00', NOW(), NOW(), 0),
(203, 1, 101, 104, '湛江振华建材有限公司',   2800.0000, 280000.00, 13.00, 20, 12, '不含安装',             0, '2026-06-11 10:00:00', NOW(), NOW(), 0);

INSERT INTO price_history (id, tenant_id, category, specification, supplier_id, supplier_name, unit_price, source_type, source_id, record_date, created_at) VALUES
(100, 1, 'IT_EQUIPMENT', '华为服务器',      100, '华为技术有限公司',      92000.0000, 'PURCHASE',  NULL, '2025-06-01', NOW()),
(101, 1, 'IT_EQUIPMENT', '华为服务器',      100, '华为技术有限公司',      88000.0000, 'PURCHASE',  NULL, '2025-12-01', NOW()),
(102, 1, 'IT_EQUIPMENT', '华为服务器',      100, '华为技术有限公司',      86000.0000, 'QUOTATION', 100,  '2026-05-12', NOW()),
(103, 1, 'FURNITURE',    '定制工位',        105, '珠海东方办公家具有限公司',2700.0000,  'PURCHASE',  NULL, '2025-09-01', NOW()),
(104, 1, 'FURNITURE',    '定制工位',        105, '珠海东方办公家具有限公司',2600.0000,  'PURCHASE',  NULL, '2026-01-01', NOW()),
(105, 1, 'FURNITURE',    '定制工位',        105, '珠海东方办公家具有限公司',2550.0000,  'QUOTATION', 101,  '2026-06-10', NOW());

-- ============================================
-- 第24部分: 通知 sys_notification
-- ============================================
INSERT INTO sys_notification (id, tenant_id, user_id, title, content, type, level, biz_type, biz_id, is_read, read_at, created_at, updated_at, deleted) VALUES
(100, 1, 102, '采购申请已提交',            '您提交的华为服务器采购申请已创建，待审批',                                    'SYSTEM',   'INFO',    'PURCHASE',    300, 0, NULL,          '2026-05-10 10:00:00', NOW(), 0),
(101, 1, 100, '您有新的审批待处理',        '王五提交的华为服务器采购申请需要审批',                                     'APPROVAL', 'INFO',    'APPROVAL',    400, 0, NULL,          '2026-05-10 10:00:01', NOW(), 0),
(102, 1, 103, '您的申请已通过',            '您提交的办公台式电脑采购申请已通过审批',                                   'APPROVAL', 'INFO',    'PURCHASE',    301, 1, '2026-04-05 14:00:00', '2026-04-05 14:00:00', NOW(), 0),
(103, 1, 104, '预警：电动车组折旧率已达80%', '纯电动公交车(10辆)折旧率已达80%，建议启动报废处置流程',                    'ALERT',    'WARNING', 'ALERT',       207, 0, NULL,          '2026-05-01 09:00:00', NOW(), 0),
(104, 1, 101, '巡检任务已完成',            '2026年第一季度资产巡检已全部完成',                                       'SYSTEM',   'INFO',    'INSPECTION',  100, 1, '2026-01-30 17:00:00', '2026-01-30 17:00:00', NOW(), 0),
(105, 1, 103, '维修申请已处理',            '您提交的服务器散热维修已处理完成',                                       'SYSTEM',   'INFO',    'MAINTENANCE', 100, 0, NULL,          '2026-06-11 16:00:00', NOW(), 0),
(106, 1, 100, '2026年Q1报表已生成',        '2026年第一季度资产汇总报表已生成',                                      'REPORT',   'INFO',    'REPORT',      100, 1, '2026-04-01 10:00:00', '2026-04-01 10:00:00', NOW(), 0),
(107, 1, 102, '资产导出任务已完成',        '资产清单导出任务已完成，请下载查看',                                     'SYSTEM',   'INFO',    'EXPORT',      100, 0, NULL,          '2026-05-15 10:00:05', NOW(), 0);

-- ============================================
-- 第25部分: 租户配置 + 用量
-- ============================================
INSERT INTO sys_tenant_config (id, tenant_id, config_key, config_value, description, created_at, updated_at) VALUES
(100, 1, 'AUTO_DEPRECIATION', 'true', '是否自动计提折旧',     NOW(), NOW()),
(101, 1, 'DEPRECIATION_DAY',  '1',    '每月折旧计提日',       NOW(), NOW()),
(102, 1, 'DEFAULT_LOCATION',  '100',  '默认资产位置ID',       NOW(), NOW());

INSERT INTO sys_tenant_usage (id, tenant_id, user_count, asset_count, storage_used_mb, last_login_time, updated_at) VALUES
(100, 1, 6, 15, 256, '2026-06-14 08:30:00', NOW());
