-- 房屋建筑类
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, certificate_no, remark) VALUES
(2001, '总部办公大楼', 'FW-2018-001', '房屋建筑类', 100, 1, '钢混/12层/8500㎡', '栋', 1, 42000000.00, 33600000.00, 8400000.00, 'IN_USE', '湛江市赤坎区人民大道100号', '办公室', '陈志远', '2018-12-01', 'PURCHASE', '粤(2018)湛江市不动产权第0100001号', '总部办公'),
(2002, '生产车间1号厂房', 'FW-2019-002', '房屋建筑类', 100, 1, '钢结构/2层/12000㎡', '栋', 1, 18000000.00, 14850000.00, 3150000.00, 'IN_USE', '湛江市东海岛工业园', '生产部', '刘伟', '2019-05-20', 'PURCHASE', '粤(2019)湛江市不动产权第0100002号', '生产厂房'),
(2003, '员工宿舍楼', 'FW-2020-003', '房屋建筑类', 101, 1, '砖混/6层/3600㎡', '栋', 1, 8500000.00, 7225000.00, 1275000.00, 'IN_USE', '湛江市麻章区', '后勤部', '赵红梅', '2020-08-01', 'PURCHASE', '粤(2020)湛江市不动产权第0100003号', '员工住宿'),
(2004, '霞山仓库', 'FW-2017-004', '房屋建筑类', 100, 1, '钢混/1层/5000㎡', '栋', 1, 6500000.00, 4550000.00, 1950000.00, 'IN_USE', '湛江市霞山区港口路', '物流部', '孙明', '2017-03-10', 'PURCHASE', '粤(2017)湛江市不动产权第0100004号', '仓储'),
(2005, '旧办公楼（待处置）', 'FW-2005-005', '房屋建筑类', 100, 1, '砖混/3层/1500㎡', '栋', 1, 1200000.00, 200000.00, 1000000.00, 'IDLE', '湛江市赤坎区老城区', NULL, NULL, '2005-01-01', 'PURCHASE', '粤(2005)湛江市不动产权第0100005号', '闲置待盘活')
ON CONFLICT (id) DO NOTHING;

-- 机器设备类
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, certificate_no, remark) VALUES
(3001, '数控加工中心CNC-500', 'SB-2021-001', '机器设备类', 100, 1, 'CNC-500/精度0.01mm', '台', 3, 3600000.00, 2520000.00, 1080000.00, 'IN_USE', '东海工业园1号厂房', '生产部', '周师傅', '2021-06-15', 'PURCHASE', 'SB-ZJ-2021001', '德国进口'),
(3002, '大型冲压机HP-800', 'SB-2020-002', '机器设备类', 100, 1, 'HP-800/800吨', '台', 2, 5600000.00, 3640000.00, 1960000.00, 'IN_USE', '东海工业园1号厂房', '生产部', '吴刚', '2020-09-01', 'PURCHASE', 'SB-ZJ-2020002', '日本进口'),
(3003, '自动化装配流水线', 'SB-2022-003', '机器设备类', 100, 1, 'AL-2000/线长60m', '条', 1, 8200000.00, 6970000.00, 1230000.00, 'IN_USE', '东海工业园2号厂房', '生产部', '郑工', '2022-03-01', 'PURCHASE', 'SB-ZJ-2022003', '国产'),
(3004, '工业机器人焊接站', 'SB-2023-004', '机器设备类', 101, 1, 'IRB-4600/6轴', '套', 4, 4800000.00, 4320000.00, 480000.00, 'IN_USE', '麻章产业园区', '生产部', '黄工', '2023-01-10', 'PURCHASE', 'SB-ZJ-2023004', 'ABB机器人'),
(3005, '老旧车床CA6140', 'SB-2015-005', '机器设备类', 100, 1, 'CA6140/φ400×1500', '台', 2, 240000.00, 80000.00, 160000.00, 'IDLE', '东海旧厂房', NULL, NULL, '2015-05-01', 'PURCHASE', 'SB-ZJ-2015005', '闲置待处置')
ON CONFLICT (id) DO NOTHING;

-- 存货类
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(4001, '特种合金钢板', 'CH-2024-001', '存货类', 100, 1, 'Q690D/20mm', '吨', 150, 1800000.00, 1800000.00, 0, 'IN_USE', '霞山仓库', '采购部', '库管员A', '2024-01-15', 'PURCHASE', '原材料'),
(4002, '电子元器件-芯片组', 'CH-2024-002', '存货类', 100, 1, 'STM32F407', '片', 5000, 250000.00, 250000.00, 0, 'IN_USE', '霞山仓库', '采购部', '库管员A', '2024-03-01', 'PURCHASE', '进口芯片'),
(4003, '半成品-电机转子', 'CH-2024-003', '存货类', 100, 1, 'Φ200×300', '件', 800, 480000.00, 480000.00, 0, 'IN_USE', '东海工业园车间', '生产部', '库管员B', '2024-04-10', 'PRODUCE', '在制品'),
(4004, '呆滞库存-旧型号轴承', 'CH-2022-004', '存货类', 100, 1, '6205-2RS/已停产', '套', 200, 60000.00, 20000.00, 40000.00, 'IDLE', '霞山仓库角落', '采购部', NULL, '2022-06-01', 'PURCHASE', '库龄超2年已跌价')
ON CONFLICT (id) DO NOTHING;

-- 无形资产
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, certificate_no, remark) VALUES
(5001, 'ERP企业资源管理系统', 'WX-2021-001', '无形资产类', 100, 1, 'SAP S/4HANA', '套', 1, 3500000.00, 2100000.00, 1400000.00, 'IN_USE', '总部机房', '信息部', '林工', '2021-03-01', 'PURCHASE', '软著SAP-2021-001', '10年摊销'),
(5002, '智能制造MES系统', 'WX-2022-002', '无形资产类', 100, 1, 'V3.0', '套', 1, 1800000.00, 1350000.00, 450000.00, 'IN_USE', '东海工业园', '信息部', '林工', '2022-06-01', 'PURCHASE', '软著MES-2022-002', '8年摊销'),
(5003, '发明专利-精密焊接工艺', 'WX-2020-003', '无形资产类', 100, 1, 'ZL202010012345.6', '项', 1, 1200000.00, 720000.00, 480000.00, 'IN_USE', NULL, '研发部', '总工办', '2020-01-15', 'RESEARCH', 'ZL202010012345.6', '自主研发')
ON CONFLICT (id) DO NOTHING;

-- 基础设施
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(6001, '东海园区道路及管网', 'JC-2020-001', '基础设施类', 100, 1, '道路3.2km/管网5.8km', '项', 1, 12500000.00, 10000000.00, 2500000.00, 'IN_USE', '东海工业园区', '物业部', '陈经理', '2020-06-01', 'BUILD', '园区配套'),
(6002, '污水处理站', 'JC-2021-002', '基础设施类', 100, 1, '日处理2000吨', '座', 1, 6800000.00, 5780000.00, 1020000.00, 'IN_USE', '东海工业园区', '环保部', '杨主管', '2021-09-01', 'BUILD', '环保设施'),
(6003, '光伏发电站（屋顶）', 'JC-2023-003', '基础设施类', 100, 1, '装机容量2MW', '项', 1, 8500000.00, 8075000.00, 425000.00, 'IN_USE', '总部办公楼顶', '能源部', '马经理', '2023-03-01', 'BUILD', '新能源项目')
ON CONFLICT (id) DO NOTHING;

-- 其他固定资产
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(7001, '办公家具套装', 'QT-2022-001', '其他', 100, 1, '实木办公桌椅柜', '套', 50, 250000.00, 175000.00, 75000.00, 'IN_USE', '总部办公楼', '办公室', '行政部', '2022-01-10', 'PURCHASE', NULL),
(7002, '中央空调系统', 'QT-2021-002', '其他', 100, 1, '格力GMV多联机', '套', 3, 450000.00, 315000.00, 135000.00, 'IN_USE', '总部办公楼', '物业部', '陈经理', '2021-07-01', 'PURCHASE', NULL),
(7003, '员工班车', 'QT-2023-003', '其他', 101, 1, '宇通ZK6115/45座', '辆', 2, 900000.00, 810000.00, 90000.00, 'IN_USE', '麻章停车场', '后勤部', '车队队长', '2023-02-01', 'PURCHASE', NULL)
ON CONFLICT (id) DO NOTHING;
