-- ===== 资产底数/清查 Mock 数据 =====

-- 土地类 (5: 土地类资产清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(1001, '东海工业园A区地块', 'TD-2020-001', '土地类', 100, 1, '50000㎡', '平方米', 1, 25000000.00, 25000000.00, 0, 'IN_USE', '湛江市东海岛', '生产部', '张建国', '2020-03-15', '2020-03-20', 'PURCHASE', '粤(2020)湛江市不动产权第0001234号', '工业用地，50年产权'),
(1002, '霞山商务区B地块', 'TD-2019-002', '土地类', 100, 1, '12000㎡', '平方米', 1, 48000000.00, 48000000.00, 0, 'IDLE', '湛江市霞山区', '办公室', '李明', '2019-06-01', '2019-06-10', 'TRANSFER', '粤(2019)湛江市不动产权第0002345号', '商业用地，40年产权，待盘活'),
(1003, '麻章仓储物流中心地块', 'TD-2021-003', '土地类', 101, 1, '35000㎡', '平方米', 1, 18000000.00, 17500000.00, 500000.00, 'IN_USE', '湛江市麻章区', '物流部', '王强', '2021-01-10', '2021-01-15', 'PURCHASE', '粤(2021)湛江市不动产权第0003456号', '仓储物流用地');

-- 房屋建筑类 (6: 房屋建筑类清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(2001, '总部办公大楼', 'FW-2018-001', '房屋建筑类', 100, 1, '钢混结构/12层/8500㎡', '栋', 1, 42000000.00, 33600000.00, 8400000.00, 'IN_USE', '湛江市赤坎区人民大道100号', '办公室', '陈志远', '2018-12-01', '2018-12-15', 'PURCHASE', '粤(2018)湛江市不动产权第0100001号', '总部办公'),
(2002, '生产车间1号厂房', 'FW-2019-002', '房屋建筑类', 100, 1, '钢结构/2层/12000㎡', '栋', 1, 18000000.00, 14850000.00, 3150000.00, 'IN_USE', '湛江市东海岛工业园', '生产部', '刘伟', '2019-05-20', '2019-06-01', 'PURCHASE', '粤(2019)湛江市不动产权第0100002号', '生产厂房'),
(2003, '员工宿舍楼', 'FW-2020-003', '房屋建筑类', 101, 1, '砖混结构/6层/3600㎡', '栋', 1, 8500000.00, 7225000.00, 1275000.00, 'IN_USE', '湛江市麻章区', '后勤部', '赵红梅', '2020-08-01', '2020-08-10', 'PURCHASE', '粤(2020)湛江市不动产权第0100003号', '员工住宿'),
(2004, '霞山仓库', 'FW-2017-004', '房屋建筑类', 100, 1, '钢混结构/1层/5000㎡', '栋', 1, 6500000.00, 4550000.00, 1950000.00, 'IN_USE', '湛江市霞山区港口路', '物流部', '孙明', '2017-03-10', '2017-03-20', 'PURCHASE', '粤(2017)湛江市不动产权第0100004号', '仓储'),
(2005, '旧办公楼（待处置）', 'FW-2005-005', '房屋建筑类', 100, 1, '砖混结构/3层/1500㎡', '栋', 1, 1200000.00, 200000.00, 1000000.00, 'IDLE', '湛江市赤坎区老城区', NULL, NULL, '2005-01-01', '2005-01-10', 'PURCHASE', '粤(2005)湛江市不动产权第0100005号', '已闲置，待盘活处置');

-- 机器设备类 (15: 大型机器设备清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(3001, '数控加工中心CNC-500', 'SB-2021-001', '机器设备类', 100, 1, 'CNC-500/精度0.01mm', '台', 3, 3600000.00, 2520000.00, 1080000.00, 'IN_USE', '东海工业园1号厂房', '生产部', '周师傅', '2021-06-15', '2021-06-20', 'PURCHASE', 'SB-ZJ-2021001', '德国进口'),
(3002, '大型冲压机HP-800', 'SB-2020-002', '机器设备类', 100, 1, 'HP-800/800吨冲压力', '台', 2, 5600000.00, 3640000.00, 1960000.00, 'IN_USE', '东海工业园1号厂房', '生产部', '吴刚', '2020-09-01', '2020-09-10', 'PURCHASE', 'SB-ZJ-2020002', '日本进口'),
(3003, '自动化装配流水线', 'SB-2022-003', '机器设备类', 100, 1, 'AL-2000/线长60m', '条', 1, 8200000.00, 6970000.00, 1230000.00, 'IN_USE', '东海工业园2号厂房', '生产部', '郑工', '2022-03-01', '2022-03-15', 'PURCHASE', 'SB-ZJ-2022003', '国产'),
(3004, '工业机器人焊接工作站', 'SB-2023-004', '机器设备类', 101, 1, 'IRB-4600/6轴', '套', 4, 4800000.00, 4320000.00, 480000.00, 'IN_USE', '麻章产业园区', '生产部', '黄工程师', '2023-01-10', '2023-01-20', 'PURCHASE', 'SB-ZJ-2023004', 'ABB机器人'),
(3005, '老旧车床CA6140', 'SB-2015-005', '机器设备类', 100, 1, 'CA6140/φ400×1500', '台', 2, 240000.00, 80000.00, 160000.00, 'IDLE', '东海旧厂房', NULL, NULL, '2015-05-01', '2015-05-10', 'PURCHASE', 'SB-ZJ-2015005', '已闲置待处置');

-- 存货类 (10: 存货清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(4001, '特种合金钢板', 'CH-2024-001', '存货类', 100, 1, 'Q690D/20mm厚', '吨', 150, 1800000.00, 1800000.00, 0, 'IN_USE', '霞山仓库', '采购部', '库管员A', '2024-01-15', '2024-01-20', 'PURCHASE', NULL, '原材料'),
(4002, '电子元器件-芯片组', 'CH-2024-002', '存货类', 100, 1, 'STM32F407系列', '片', 5000, 250000.00, 250000.00, 0, 'IN_USE', '霞山仓库', '采购部', '库管员A', '2024-03-01', '2024-03-05', 'PURCHASE', NULL, '进口芯片'),
(4003, '半成品-电机转子', 'CH-2024-003', '存货类', 100, 1, 'Φ200×300', '件', 800, 480000.00, 480000.00, 0, 'IN_USE', '东海工业园车间', '生产部', '库管员B', '2024-04-10', '2024-04-12', 'PRODUCE', NULL, '在制品'),
(4004, '呆滞库存-旧型号轴承', 'CH-2022-004', '存货类', 100, 1, '6205-2RS/已停产', '套', 200, 60000.00, 20000.00, 40000.00, 'IDLE', '霞山仓库角落', '采购部', NULL, '2022-06-01', '2022-06-05', 'PURCHASE', NULL, '库龄超过2年，已计提跌价准备');

-- 无形资产类 (11: 无形资产清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(5001, 'ERP企业资源管理系统', 'WX-2021-001', '无形资产类', 100, 1, 'SAP S/4HANA', '套', 1, 3500000.00, 2100000.00, 1400000.00, 'IN_USE', '总部机房', '信息部', '林工', '2021-03-01', '2021-03-15', 'PURCHASE', '软著登字第SAP-2021-001号', '10年摊销'),
(5002, '智能制造MES系统', 'WX-2022-002', '无形资产类', 100, 1, 'V3.0', '套', 1, 1800000.00, 1350000.00, 450000.00, 'IN_USE', '东海工业园', '信息部', '林工', '2022-06-01', '2022-06-10', 'PURCHASE', '软著登字第MES-2022-002号', '8年摊销'),
(5003, '发明专利-精密焊接工艺', 'WX-2020-003', '无形资产类', 100, 1, 'ZL202010012345.6', '项', 1, 1200000.00, 720000.00, 480000.00, 'IN_USE', NULL, '研发部', '总工办', '2020-01-15', '2020-02-01', 'RESEARCH', 'ZL202010012345.6', '自主研发展专利');

-- 基础设施类 (16: 基础设施资产清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(6001, '东海园区道路及管网', 'JC-2020-001', '基础设施类', 100, 1, '道路总长3.2km/管网5.8km', '项', 1, 12500000.00, 10000000.00, 2500000.00, 'IN_USE', '东海工业园区', '物业部', '陈经理', '2020-06-01', '2020-06-15', 'BUILD', NULL, '园区配套'),
(6002, '污水处理站', 'JC-2021-002', '基础设施类', 100, 1, '日处理2000吨', '座', 1, 6800000.00, 5780000.00, 1020000.00, 'IN_USE', '东海工业园区', '环保部', '杨主管', '2021-09-01', '2021-09-20', 'BUILD', NULL, '环保设施'),
(6003, '光伏发电站（屋顶）', 'JC-2023-003', '基础设施类', 100, 1, '装机容量2MW', '项', 1, 8500000.00, 8075000.00, 425000.00, 'IN_USE', '总部办公楼顶', '能源部', '马经理', '2023-03-01', '2023-03-15', 'BUILD', NULL, '新能源项目');

-- 其他固定资产 (18: 其他固定资产清查明细表)
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, registration_date, source_type, certificate_no, remark)
VALUES
(7001, '办公家具套装', 'QT-2022-001', '其他', 100, 1, '实木办公桌椅柜', '套', 50, 250000.00, 175000.00, 75000.00, 'IN_USE', '总部办公楼', '办公室', '行政部', '2022-01-10', '2022-01-15', 'PURCHASE', NULL, NULL),
(7002, '中央空调系统', 'QT-2021-002', '其他', 100, 1, '格力GMV多联机', '套', 3, 450000.00, 315000.00, 135000.00, 'IN_USE', '总部办公楼', '物业部', '陈经理', '2021-07-01', '2021-07-10', 'PURCHASE', NULL, NULL),
(7003, '员工班车', 'QT-2023-003', '其他', 101, 1, '宇通ZK6115/45座', '辆', 2, 900000.00, 810000.00, 90000.00, 'IN_USE', '麻章停车场', '后勤部', '车队队长', '2023-02-01', '2023-02-10', 'PURCHASE', NULL, NULL);

-- ===== 巡检异常数据（3: 问题资产及整治清单） =====
INSERT INTO inspection_abnormal (id, task_id, asset_code, asset_name, abnormal_type, description, amount, found_date, measure, responsible_dept, status, completed_date, remark, org_id, tenant_id, created_at, updated_at, deleted)
VALUES
(8001, 1, 'TD-2019-002', '霞山商务区B地块', 'IDLE_WASTE', '土地闲置超过3年，未开发利用', 48000000.00, '2024-12-01', '启动盘活利用程序，寻求合作开发', '资产管理部', 'PENDING', NULL, '需上报国资委', 100, 1, NOW(), NOW(), 0),
(8002, 2, 'FW-2005-005', '旧办公楼（待处置）', 'DISPOSAL_NEEDED', '楼龄超过20年，存在安全隐患，建议报废处置', 200000.00, '2024-11-15', '编制报废处置方案并报批', '后勤部', 'IN_PROGRESS', NULL, '待审批', 100, 1, NOW(), NOW(), 0),
(8003, 3, 'SB-2015-005', '老旧车床CA6140', 'IDLE_WASTE', '设备老化闲置，生产效率已不满足要求', 80000.00, '2025-01-10', '评估残值，走报废处置流程', '生产部', 'IN_PROGRESS', NULL, NULL, 100, 1, NOW(), NOW(), 0),
(8004, 4, 'CH-2022-004', '呆滞库存-旧型号轴承', 'INVENTORY_OVERSTOCK', '库存积压超过2年，型号已淘汰', 20000.00, '2025-01-20', '折价处理或报废', '采购部', 'PENDING', NULL, NULL, 100, 1, NOW(), NOW(), 0);

-- ===== 折旧记录 =====
INSERT INTO depreciation (id, asset_id, depreciation_amount, depreciation_date, before_value, after_value, period)
VALUES
(9001, 2001, 70000.00, '2026-01-31', 33740000.00, 33670000.00, '2026-01'),
(9002, 2001, 70000.00, '2026-02-28', 33670000.00, 33600000.00, '2026-02'),
(9003, 2002, 37500.00, '2026-01-31', 14887500.00, 14850000.00, '2026-01'),
(9004, 3001, 90000.00, '2026-01-31', 2610000.00, 2520000.00, '2026-01'),
(9005, 3002, 140000.00, '2026-01-31', 3780000.00, 3640000.00, '2026-01'),
(9006, 4004, 3333.33, '2026-01-31', 23333.33, 20000.00, '2026-01'),
(9007, 5001, 35000.00, '2026-01-31', 2135000.00, 2100000.00, '2026-01');

-- ===== 7张新实体表 Mock数据 =====

-- 股权类投资 (7: 股权类清查明细表)
INSERT INTO equity_investment (id, enterprise_name, credit_code, invest_date, invest_method, share_ratio, invest_amount, cumulative_dividend, book_value, fair_value, is_impaired, impairment_amount, enterprise_status, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(10001, '湛江港务发展有限公司', '91440800MA12345678', '2019-06-01', '货币出资', 35.00, 12000000.00, 4200000.00, 12000000.00, 14800000.00, FALSE, 0, '正常经营', 100, 1, '参股企业', NOW(), NOW(), 0),
(10002, '南粤新能源科技有限公司', '91440800MA23456789', '2022-03-15', '货币出资', 51.00, 25500000.00, 3100000.00, 25500000.00, 27200000.00, FALSE, 0, '正常经营', 100, 1, '控股子公司', NOW(), NOW(), 0),
(10003, '湛江生物科技合伙企业', '91440800MA34567890', '2018-01-10', '无形资产出资', 20.00, 5000000.00, 800000.00, 5000000.00, 3800000.00, TRUE, 1200000.00, '经营困难', 101, 1, '已计提减值', NOW(), NOW(), 0);

-- 债权类 (8: 债权类清查明细表)
INSERT INTO creditor_rights (id, creditor_code, debtor_name, rights_type, amount, formed_date, aging, bad_debt_provision, estimated_recoverable, collection_status, contract_no, is_litigation, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(11001, 'ZQ-2024-001', '宏达建设工程有限公司', '应收账款', 3800000.00, '2024-06-15', '1-2年', 380000.00, 3420000.00, '催收中', 'HT-2024-068', FALSE, 100, 1, '工程款', NOW(), NOW(), 0),
(11002, 'ZQ-2025-002', '中远物流运输有限公司', '应收账款', 1200000.00, '2025-03-20', '1年以内', 60000.00, 1140000.00, '正常履约', 'HT-2025-012', FALSE, 100, 1, '物流服务费', NOW(), NOW(), 0),
(11003, 'ZQ-2023-003', '鑫达贸易有限公司', '其他应收款', 800000.00, '2023-08-01', '2-3年', 400000.00, 400000.00, '催收中', 'HT-2023-045', TRUE, 100, 1, '已提起诉讼', NOW(), NOW(), 0),
(11004, 'ZQ-2024-004', '市政府采购中心', '合同资产', 5600000.00, '2024-11-01', '1年以内', 0, 5600000.00, '正常履约', 'ZC-2024-089', FALSE, 101, 1, '政府采购项目', NOW(), NOW(), 0);

-- 私募基金投资 (9: 企业私募基金投资情况调查表)
INSERT INTO pe_fund_investment (id, fund_name, fund_manager, fund_type, subscribed_amount, paid_amount, invest_date, fund_duration, current_nav, cumulative_return, is_violation, record_no, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(12001, '粤西产业发展基金', '广发信德投资管理有限公司', '股权投资基金', 30000000.00, 30000000.00, '2022-01-15', '5+2年', 35400000.00, 5400000.00, FALSE, 'P2022-GD001', 100, 1, '政府引导基金子基金', NOW(), NOW(), 0),
(12002, '湛江科技创新投资基金', '深圳市创新投资集团有限公司', '创业投资基金', 15000000.00, 10000000.00, '2023-06-01', '7年', 11200000.00, 1200000.00, FALSE, 'P2023-GD015', 100, 1, '第二期出资待缴', NOW(), NOW(), 0);

-- 特许经营权 (12: 特许经营权清查明细表)
INSERT INTO franchise_right (id, right_name, authorizer, start_date, end_date, authorized_area, business_scope, authorization_fee, annual_fee, is_expired, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(13001, '湛江港危险品仓储经营许可', '湛江市交通运输局', '2020-01-01', '2030-12-31', '湛江港区', '危险品仓储及中转服务', 2000000.00, 200000.00, FALSE, 100, 1, '10年期特许经营', NOW(), NOW(), 0),
(13002, '城市建筑垃圾资源化处理特许经营权', '湛江市城市管理局', '2022-06-01', '2042-05-31', '湛江市全域', '建筑垃圾回收、处理及资源化利用', 5000000.00, 500000.00, FALSE, 101, 1, '20年特许经营', NOW(), NOW(), 0);

-- 数据资源/资产 (13: 数据资源/资产清查明细表)
INSERT INTO data_asset (id, data_name, data_type, data_volume, storage_method, security_level, is_in_balance_sheet, balance_sheet_value, usage_scenario, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(14001, '企业运营数据集', '结构化数据', '50TB', '私有云存储', '内部', TRUE, 3200000.00, '经营分析、决策支持', 100, 1, '含ERP/MES/CRM系统数据', NOW(), NOW(), 0),
(14002, '工业物联网传感器数据', '时序数据', '120TB', '边缘计算+云端', '机密', FALSE, NULL, '设备预测性维护、工艺优化', 100, 1, '产线IoT数据', NOW(), NOW(), 0);

-- 自然资源资产 (14: 自然资源资产清查明细表)
INSERT INTO natural_resource (id, resource_name, resource_type, location, area_or_reserve, unit, certificate_no, acquisition_method, useful_life_years, book_value, appraised_value, is_developed, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(15001, '东海岛南部海域养殖区', '水域', '湛江市东海岛南部', '500公顷', '公顷', '粤海域使用(2020)第008号', '政府划拨', 20, 3800000.00, 5200000.00, TRUE, 100, 1, '海水养殖', NOW(), NOW(), 0),
(15002, '廉江矿区建筑用花岗岩', '矿产', '湛江市廉江市', '储量约200万吨', '吨', '采矿证C4400002021080000123', '招拍挂取得', 10, 8500000.00, 12000000.00, TRUE, 101, 1, '建筑石料开采', NOW(), NOW(), 0);

-- 货币资金 (17: 货币资金清查明细表)
INSERT INTO cash_account (id, account_name, bank_name, account_no, currency, book_balance, statement_balance, diff_amount, diff_reason, account_type, is_restricted, restricted_amount, reconciliation_date, org_id, tenant_id, remark, created_at, updated_at, deleted)
VALUES
(16001, '基本存款账户', '中国工商银行湛江分行', '2015022020201035678', 'CNY', 25800000.00, 25800000.00, 0, NULL, '基本户', FALSE, 0, '2026-05-31', 100, 1, NULL, NOW(), NOW(), 0),
(16002, '一般存款账户', '中国建设银行湛江赤坎支行', '4405018020201045678', 'CNY', 8900000.00, 8885000.00, 15000.00, '银行手续费未入账', '一般户', FALSE, 0, '2026-05-31', 100, 1, '差异待调节', NOW(), NOW(), 0),
(16003, '外汇资本金账户', '中国银行湛江分行', '68901234567890123', 'USD', 15000000.00, 15000000.00, 0, NULL, '专户', TRUE, 15000000.00, '2026-05-31', 100, 1, '外资项目专用账户', NOW(), NOW(), 0),
(16004, '零余额账户', '中国农业银行湛江分行', '6228480123456789012', 'CNY', 1200000.00, 1200000.00, 0, NULL, '一般户', FALSE, 0, '2026-05-31', 101, 1, NULL, NOW(), NOW(), 0);
