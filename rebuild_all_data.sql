-- ===== Clear and rebuild =====
DELETE FROM export_task;
DELETE FROM asset WHERE id >= 1000;
DELETE FROM equity_investment;
DELETE FROM creditor_rights;
DELETE FROM pe_fund_investment;
DELETE FROM franchise_right;
DELETE FROM data_asset;
DELETE FROM natural_resource;
DELETE FROM cash_account;
DELETE FROM depreciation WHERE id >= 9000;

-- Update original 15 assets to new org IDs
UPDATE asset SET org_id = 200, tenant_id = 1 WHERE id IN (200, 201, 208, 209, 211);
UPDATE asset SET org_id = 300, tenant_id = 1 WHERE id IN (202, 205, 213);
UPDATE asset SET org_id = 400, tenant_id = 1 WHERE id IN (203, 204);
UPDATE asset SET org_id = 500, tenant_id = 1 WHERE id IN (206, 207, 214);
UPDATE asset SET org_id = 600, tenant_id = 1 WHERE id IN (210, 212);

-- ===== 湛江市城市发展集团 (org_id=200) - 城发建筑、产投、基建 =====
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(1001, '城发总部办公楼', 'CD-2019-001', 'REAL_ESTATE', 200, 1, '钢混/15层/10000㎡', '栋', 1, 58000000.00, 46400000.00, 11600000.00, 'IN_USE', '湛江市赤坎区人民大道200号', '办公室', '陈志远', '2019-03-01', 'PURCHASE', '集团总部'),
(1002, '西城新区储备地块A区', 'CD-2020-002', 'LAND', 20008, 1, '80000㎡', '平方米', 1, 36000000.00, 36000000.00, 0, 'IN_USE', '湛江市西城区', '开发部', '劳晖杰', '2020-06-15', 'PURCHASE', '湛江市西城新区建设投资有限公司'),
(1003, '高铁新城站前广场地块', 'CD-2022-003', 'LAND', 20021, 1, '45000㎡', '平方米', 1, 28000000.00, 28000000.00, 0, 'IN_USE', '湛江市高铁新城', '项目部', '王启丞', '2022-09-01', 'PURCHASE', '湛江市高铁新城建设投资有限公司'),
(1004, '广湛合作产业园标准厂房', 'CD-2023-004', 'REAL_ESTATE', 20010, 1, '钢混/3层/20000㎡', '栋', 3, 45000000.00, 42750000.00, 2250000.00, 'IN_USE', '广湛合作产业园', '产业部', '林维杰', '2023-06-01', 'BUILD', '广东省广湛合作产业园投资开发有限公司'),
(1005, '海上风电安装平台', 'CD-2024-005', 'EQUIPMENT', 20014, 1, '自升式/1200吨', '台', 1, 85000000.00, 80750000.00, 4250000.00, 'IN_USE', '湛江外海风电场', '新能源部', '姚立鑫', '2024-01-15', 'PURCHASE', '广东省北部湾海上风电发展有限公司'),
(1006, '智云数据中心服务器集群', 'CD-2023-006', 'IT_EQUIPMENT', 20025, 1, 'HPC/500节点', '套', 1, 12000000.00, 9600000.00, 2400000.00, 'IN_USE', '湛江数据中心', '信息部', '劳晖杰', '2023-12-01', 'PURCHASE', '湛江市智云投资有限公司'),
(1007, '城发置地滨海花园项目', 'CD-2024-007', 'REAL_ESTATE', 20017, 1, '在建/商住/80000㎡', '项', 1, 180000000.00, 180000000.00, 0, 'IN_USE', '湛江市海滨大道', '开发部', '范珊珊', '2024-06-01', 'BUILD', '湛江城发置地有限公司');

-- ===== 湛江市海洋与农业投资集团 (org_id=300) =====
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(2001, '海洋集团总部大楼', 'HY-2020-001', 'REAL_ESTATE', 300, 1, '钢混/10层/6000㎡', '栋', 1, 32000000.00, 27200000.00, 4800000.00, 'IN_USE', '湛江市开发区海滨大道300号', '办公室', '张海', '2020-08-01', 'PURCHASE', '集团总部'),
(2002, '蓝色海洋产业研发基地', 'HY-2023-002', 'REAL_ESTATE', 30009, 1, '钢混/2层/5000㎡', '栋', 1, 28000000.00, 26600000.00, 1400000.00, 'IN_USE', '湛江市东海岛', '研发部', '黄艺', '2023-09-01', 'BUILD', '湛江市蓝色海洋产业发展有限公司'),
(2003, '金涛渔业养殖基地', 'HY-2024-003', 'INFRASTRUCTURE', 30014, 1, '海域/200公顷', '项', 1, 15000000.00, 14500000.00, 500000.00, 'IN_USE', '湛江市东海岛南部海域', '渔业部', '肖素晖', '2024-10-01', 'PURCHASE', '湛江市金涛渔业发展有限公司'),
(2004, '金海粮油仓储物流中心', 'HY-2019-004', 'REAL_ESTATE', 30004, 1, '钢混/仓库/8000㎡', '栋', 1, 22000000.00, 17600000.00, 4400000.00, 'IN_USE', '湛江港口区', '仓储部', '邓都亮', '2019-05-01', 'PURCHASE', '广东湛江金海粮油有限公司'),
(2005, '食品冷链加工生产线', 'HY-2022-005', 'EQUIPMENT', 30001, 1, '冷链/日处理50吨', '套', 1, 8500000.00, 5950000.00, 2550000.00, 'IN_USE', '湛江市食品工业园', '生产部', '黄瑜', '2022-03-15', 'PURCHASE', '湛江市食品企业有限公司');

-- ===== 湛江市旅游投资集团 (org_id=400) =====
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(3001, '湛旅集团办公楼', 'LY-2021-001', 'REAL_ESTATE', 400, 1, '框架/8层/4000㎡', '栋', 1, 18000000.00, 15300000.00, 2700000.00, 'IN_USE', '湛江市霞山区海滨路400号', '办公室', '蔡晓明', '2021-05-01', 'PURCHASE', '集团总部'),
(3002, '蓝月湾温泉度假酒店', 'LY-2018-002', 'REAL_ESTATE', 40008, 1, '框架/5层/12000㎡', '栋', 1, 35000000.00, 22750000.00, 12250000.00, 'IN_USE', '湛江市海滨温泉路', '酒店部', '陈龙', '2018-12-01', 'PURCHASE', '湛江市蓝月湾温泉开发有限公司'),
(3003, '南极村滨海旅游设施', 'LY-2023-003', 'INFRASTRUCTURE', 40009, 1, '游客中心+栈道/3000㎡', '项', 1, 12000000.00, 11400000.00, 600000.00, 'IN_USE', '湛江市徐闻县南极村', '景区部', '谢哉生', '2023-03-10', 'BUILD', '湛江南极村滨海旅游开发有限公司'),
(3004, '国际会展中心', 'LY-2019-004', 'REAL_ESTATE', 40003, 1, '钢混/1层/20000㎡', '栋', 1, 65000000.00, 52000000.00, 13000000.00, 'IN_USE', '湛江市开发区会展路', '会展部', '徐祝睿', '2019-10-01', 'BUILD', '广东臻创国际会展有限公司'),
(3005, '军博园展览设施', 'LY-2022-005', 'EQUIPMENT', 40004, 1, '军事展览/5000㎡', '项', 1, 8500000.00, 6800000.00, 1700000.00, 'IN_USE', '湛江市坡头区', '展览部', '李京', '2022-07-01', 'PURCHASE', '湛江军博园旅游文化发展有限公司');

-- ===== 湛江市公共服务集团 (org_id=500) =====
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(4001, '公交集团运营中心', 'GF-2020-001', 'REAL_ESTATE', 500, 1, '钢混/3层/3000㎡', '栋', 1, 12000000.00, 9600000.00, 2400000.00, 'IN_USE', '湛江市赤坎区公交路1号', '运营部', '潘伟文', '2020-04-01', 'PURCHASE', '集团总部'),
(4002, '新能源公交车辆(50辆)', 'GF-2024-002', 'VEHICLE', 50006, 1, '纯电动/10米级', '辆', 50, 25000000.00, 23750000.00, 1250000.00, 'IN_USE', '公交各停车场', '车队', '李海明', '2024-01-15', 'PURCHASE', '湛江市公服巴士有限公司'),
(4003, '公交车辆维修中心', 'GF-2021-003', 'EQUIPMENT', 50003, 1, '维修设备全套', '套', 1, 5000000.00, 3500000.00, 1500000.00, 'IN_USE', '湛江市公交维修基地', '维修部', '沈江', '2021-06-01', 'PURCHASE', '湛江市公共服务集团汽车维修有限公司');

-- ===== 湛江市资产运营集团 (org_id=600) =====
INSERT INTO asset (id, name, asset_code, category, org_id, tenant_id, specification, unit, quantity, original_value, current_value, accumulated_depreciation, use_status, location, use_department, custodian, purchase_date, source_type, remark) VALUES
(5001, '湛资集团办公楼', 'ZC-2020-001', 'REAL_ESTATE', 600, 1, '框架/12层/8000㎡', '栋', 1, 25000000.00, 21250000.00, 3750000.00, 'IN_USE', '湛江市开发区人民大道600号', '办公室', '江华武', '2020-01-15', 'PURCHASE', '集团总部'),
(5002, '湛江吉民药业生产基地', 'ZC-2005-002', 'REAL_ESTATE', 60020, 1, '厂房/5000㎡', '栋', 1, 8500000.00, 3400000.00, 5100000.00, 'IN_USE', '湛江市霞山区', '生产部', '官东', '2005-06-01', 'PURCHASE', '广东湛江吉民药业股份有限公司'),
(5003, '建筑工程集团设备中心', 'ZC-2019-003', 'EQUIPMENT', 60178, 1, '塔吊/搅拌站/施工机械', '批', 1, 18000000.00, 10800000.00, 7200000.00, 'IN_USE', '湛江市各项目部', '工程部', '汤科', '2019-08-01', 'PURCHASE', '湛江市建筑工程集团公司'),
(5004, '湛江玻璃厂生产线', 'ZC-2010-004', 'EQUIPMENT', 60116, 1, '玻璃窑炉/制瓶机', '套', 1, 6000000.00, 1500000.00, 4500000.00, 'IDLE', '湛江市坡头区', NULL, NULL, '2010-05-01', 'PURCHASE', '广东湛江玻璃厂'),
(5005, '广东省八建集团基地', 'ZC-2000-005', 'REAL_ESTATE', 60179, 1, '办公楼+仓库/6000㎡', '栋', 1, 12000000.00, 4800000.00, 7200000.00, 'IN_USE', '湛江市赤坎区', '工程部', '陈顺利', '2000-01-01', 'PURCHASE', '广东省八建集团有限公司');

-- ===== 股权类 =====
INSERT INTO equity_investment (id, enterprise_name, credit_code, invest_date, invest_method, share_ratio, invest_amount, cumulative_dividend, book_value, fair_value, is_impaired, impairment_amount, enterprise_status, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(10001, '湛江港务发展有限公司', '91440800MA12345678', '2019-06-01', '货币出资', 35.00, 12000000.00, 4200000.00, 12000000.00, 14800000.00, FALSE, 0, '正常经营', 200, 1, '城市发展集团参股', NOW(), NOW(), 0),
(10002, '南粤新能源科技有限公司', '91440800MA23456789', '2022-03-15', '货币出资', 51.00, 25500000.00, 3100000.00, 25500000.00, 27200000.00, FALSE, 0, '正常经营', 200, 1, '城市发展集团控股', NOW(), NOW(), 0),
(10003, '湛江生物科技合伙企业', '91440800MA34567890', '2018-01-10', '无形资产出资', 20.00, 5000000.00, 800000.00, 5000000.00, 3800000.00, TRUE, 1200000.00, '经营困难', 300, 1, '海洋农业集团', NOW(), NOW(), 0);

-- ===== 债权类 =====
INSERT INTO creditor_rights (id, creditor_code, debtor_name, rights_type, amount, formed_date, aging, bad_debt_provision, estimated_recoverable, collection_status, contract_no, is_litigation, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(11001, 'ZQ-2024-001', '宏达建设工程有限公司', '应收账款', 3800000.00, '2024-06-15', '1-2年', 380000.00, 3420000.00, '催收中', 'HT-2024-068', FALSE, 200, 1, '城发集团工程款', NOW(), NOW(), 0),
(11002, 'ZQ-2025-002', '中远物流运输有限公司', '应收账款', 1200000.00, '2025-03-20', '1年以内', 60000.00, 1140000.00, '正常履约', 'HT-2025-012', FALSE, 600, 1, '资产集团物流费', NOW(), NOW(), 0),
(11003, 'ZQ-2023-003', '鑫达贸易有限公司', '其他应收款', 800000.00, '2023-08-01', '2-3年', 400000.00, 400000.00, '催收中', 'HT-2023-045', TRUE, 600, 1, '已提起诉讼', NOW(), NOW(), 0),
(11004, 'ZQ-2024-004', '市政府采购中心', '合同资产', 5600000.00, '2024-11-01', '1年以内', 0, 5600000.00, '正常履约', 'ZC-2024-089', FALSE, 400, 1, '旅投集团政府采购', NOW(), NOW(), 0);

-- ===== 私募基金 =====
INSERT INTO pe_fund_investment (id, fund_name, fund_manager, fund_type, subscribed_amount, paid_amount, invest_date, fund_duration, current_nav, cumulative_return, is_violation, record_no, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(12001, '粤西产业发展基金', '广发信德投资管理有限公司', '股权投资基金', 30000000.00, 30000000.00, '2022-01-15', '5+2年', 35400000.00, 5400000.00, FALSE, 'P2022-GD001', 200, 1, '城发集团', NOW(), NOW(), 0),
(12002, '湛江科技创新投资基金', '深圳市创新投资集团有限公司', '创业投资基金', 15000000.00, 10000000.00, '2023-06-01', '7年', 11200000.00, 1200000.00, FALSE, 'P2023-GD015', 600, 1, '资产集团', NOW(), NOW(), 0);

-- ===== 特许经营权 =====
INSERT INTO franchise_right (id, right_name, authorizer, start_date, end_date, authorized_area, business_scope, authorization_fee, annual_fee, is_expired, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(13001, '湛江港危险品仓储经营许可', '湛江市交通运输局', '2020-01-01', '2030-12-31', '湛江港区', '危险品仓储及中转服务', 2000000.00, 200000.00, FALSE, 300, 1, '海洋农业集团', NOW(), NOW(), 0),
(13002, '城市建筑垃圾资源化处理特许经营权', '湛江市城市管理局', '2022-06-01', '2042-05-31', '湛江市全域', '建筑垃圾回收处理及资源化利用', 5000000.00, 500000.00, FALSE, 500, 1, '公共服务集团', NOW(), NOW(), 0);

-- ===== 数据资源 =====
INSERT INTO data_asset (id, data_name, data_type, data_volume, storage_method, security_level, is_in_balance_sheet, balance_sheet_value, usage_scenario, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(14001, '城发集团运营数据集', '结构化数据', '50TB', '私有云存储', '内部', TRUE, 3200000.00, '经营分析、决策支持', 200, 1, '含ERP/MES/CRM数据', NOW(), NOW(), 0),
(14002, '海洋渔业物联网数据', '时序数据', '120TB', '边缘计算+云端', '机密', FALSE, NULL, '海洋牧场监测、渔船调度', 300, 1, '海洋集团IoT数据', NOW(), NOW(), 0);

-- ===== 自然资源 =====
INSERT INTO natural_resource (id, resource_name, resource_type, location, area_or_reserve, unit, certificate_no, acquisition_method, useful_life_years, book_value, appraised_value, is_developed, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(15001, '东海岛南部海域养殖区', '水域', '湛江市东海岛南部', '500公顷', '公顷', '粤海域使用(2020)第008号', '政府划拨', 20, 3800000.00, 5200000.00, TRUE, 300, 1, '海洋集团海水养殖', NOW(), NOW(), 0),
(15002, '廉江矿区建筑用花岗岩', '矿产', '湛江市廉江市', '约200万吨', '吨', '采矿证C4400002021080000123', '招拍挂取得', 10, 8500000.00, 12000000.00, TRUE, 200, 1, '城发集团建材', NOW(), NOW(), 0);

-- ===== 货币资金 =====
INSERT INTO cash_account (id, account_name, bank_name, account_no, currency, book_balance, statement_balance, diff_amount, diff_reason, account_type, is_restricted, restricted_amount, reconciliation_date, org_id, tenant_id, remark, created_at, updated_at, deleted) VALUES
(16001, '城发集团基本户', '中国工商银行湛江分行', '2015022020201035001', 'CNY', 28000000.00, 28000000.00, 0, NULL, '基本户', FALSE, 0, '2026-05-31', 200, 1, NULL, NOW(), NOW(), 0),
(16002, '海洋集团基本户', '中国建设银行湛江赤坎支行', '4405018020201045002', 'CNY', 9200000.00, 9185000.00, 15000.00, '银行手续费未入账', '一般户', FALSE, 0, '2026-05-31', 300, 1, '差异待调节', NOW(), NOW(), 0),
(16003, '旅投集团外汇账户', '中国银行湛江分行', '68901234567890503', 'USD', 3200000.00, 3200000.00, 0, NULL, '专户', TRUE, 3200000.00, '2026-05-31', 400, 1, '旅游项目专用', NOW(), NOW(), 0),
(16004, '公服集团零余额户', '中国农业银行湛江分行', '6228480123456785004', 'CNY', 1800000.00, 1800000.00, 0, NULL, '一般户', FALSE, 0, '2026-05-31', 500, 1, NULL, NOW(), NOW(), 0);

SELECT 'Data rebuilt: ' || NOW();
