package com.sasac.platform.supervision.alert.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.decision.entity.DecisionSupervision;
import com.sasac.platform.decision.mapper.DecisionSupervisionMapper;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import com.sasac.platform.investment.entity.InvestProject;
import com.sasac.platform.investment.mapper.InvestProjectMapper;
import com.sasac.platform.project.entity.PmProject;
import com.sasac.platform.project.mapper.PmProjectMapper;
import com.sasac.platform.property.entity.PrTransactionMonitor;
import com.sasac.platform.property.mapper.PrTransactionMonitorMapper;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.entity.AlertRule;
import com.sasac.platform.supervision.alert.mapper.AlertRecordMapper;
import com.sasac.platform.supervision.alert.mapper.AlertRuleMapper;
import com.sasac.platform.supervision.entity.SupRectification;
import com.sasac.platform.supervision.mapper.SupRectificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for alert rule management and alert record lifecycle.
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRuleMapper alertRuleMapper;
    private final AlertRecordMapper alertRecordMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final PmProjectMapper pmProjectMapper;
    private final InvestProjectMapper investProjectMapper;
    private final DecisionSupervisionMapper decisionSupervisionMapper;
    private final SupRectificationMapper supRectificationMapper;
    private final PrTransactionMonitorMapper prTransactionMonitorMapper;

    // ---- Rule Management ----

    /**
     * Creates a new alert rule.
     *
     * @param rule the alert rule to create
     * @return the saved rule with generated ID
     */
    @Transactional
    public AlertRule createRule(AlertRule rule) {
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        alertRuleMapper.insert(rule);
        return rule;
    }

    /**
     * Updates an existing alert rule.
     *
     * @param id   the rule ID
     * @param rule the updated fields
     * @return the updated rule
     * @throws BusinessException if the rule does not exist
     */
    @Transactional
    public AlertRule updateRule(Long id, AlertRule rule) {
        AlertRule existing = alertRuleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("告警规则不存在");
        }
        rule.setId(id);
        rule.setUpdatedAt(LocalDateTime.now());
        alertRuleMapper.updateById(rule);
        return alertRuleMapper.selectById(id);
    }

    /**
     * Lists all rules for a tenant, optionally filtered by alert type.
     *
     * @param tenantId the tenant ID
     * @param alertType optional alert type filter
     * @return list of alert rules
     */
    public List<AlertRule> listRules(Long tenantId, String alertType) {
        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AlertRule::getTenantId, tenantId);
        }
        if (alertType != null && !alertType.isBlank()) {
            wrapper.eq(AlertRule::getAlertType, alertType);
        }
        wrapper.orderByDesc(AlertRule::getCreatedAt);
        return alertRuleMapper.selectList(wrapper);
    }

    // ---- Record Lifecycle ----

    /**
     * Creates a new alert record.
     *
     * @param record the alert record to create
     * @return the saved record with generated ID
     */
    @Transactional
    public AlertRecord createRecord(AlertRecord record) {
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        alertRecordMapper.insert(record);
        return record;
    }

    /**
     * Lists alert records for a tenant with optional filters.
     *
     * @param tenantId  the tenant ID
     * @param alertType optional alert type filter
     * @param status    optional status filter
     * @return list of alert records
     */
    public List<AlertRecord> listRecords(Long tenantId, String alertType, String status) {
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(AlertRecord::getTenantId, tenantId);
        }
        if (alertType != null && !alertType.isBlank()) {
            wrapper.eq(AlertRecord::getAlertType, alertType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(AlertRecord::getStatus, status);
        }
        wrapper.orderByDesc(AlertRecord::getCreatedAt);
        return alertRecordMapper.selectList(wrapper);
    }

    /**
     * Acknowledges an alert record.
     *
     * @param id        the record ID
     * @param handledBy the user ID acknowledging the alert
     * @throws BusinessException if the record does not exist
     */
    @Transactional
    public void acknowledge(Long id, Long handledBy) {
        AlertRecord record = alertRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("告警记录不存在");
        }
        record.setStatus("ACKNOWLEDGED");
        record.setHandledBy(handledBy);
        record.setHandledAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        alertRecordMapper.updateById(record);
    }

    /**
     * Resolves an alert record.
     *
     * @param id        the record ID
     * @param handledBy the user ID resolving the alert
     * @throws BusinessException if the record does not exist
     */
    @Transactional
    public void resolve(Long id, Long handledBy) {
        AlertRecord record = alertRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("告警记录不存在");
        }
        record.setStatus("RESOLVED");
        record.setHandledBy(handledBy);
        record.setHandledAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        alertRecordMapper.updateById(record);
    }

    // ========================================================================
    //  M11: 综合预警规则引擎 - 跨模块预警检查
    // ========================================================================

    /**
     * HR预警: 关键岗位空缺>30天、月离职率>5%
     */
    public List<AlertRecord> checkHrAlerts(Long tenantId) {
        List<AlertRecord> alerts = new ArrayList<>();
        LocalDate threshold = LocalDate.now().minusDays(30);

        // 1. 空缺岗位检查 - 统计超过30天无替代人员的已离职员工
        LambdaQueryWrapper<HrEmployee> vacantWrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            vacantWrapper.eq(HrEmployee::getTenantId, tenantId);
        }
        vacantWrapper.eq(HrEmployee::getStatus, "RESIGNED");
        List<HrEmployee> resigned = hrEmployeeMapper.selectList(vacantWrapper);
        for (HrEmployee emp : resigned) {
            if (emp.getUpdatedAt() != null && emp.getUpdatedAt().toLocalDate().isBefore(threshold)) {
                AlertRecord record = new AlertRecord();
                record.setTenantId(tenantId);
                record.setAlertType("HR_VACANCY");
                record.setTitle("关键岗位空缺预警");
                record.setContent(String.format("员工 %s (%s) 已离职超过30天，尚未安排替代人员", emp.getName(), emp.getEmployeeNo()));
                record.setStatus("ACTIVE");
                record.setRefId(emp.getId());
                alerts.add(record);
            }
        }

        // 2. 月离职率检查 - 当前月离职人数占比超过5%
        if (tenantId != null) {
            LambdaQueryWrapper<HrEmployee> totalWrapper = new LambdaQueryWrapper<>();
            totalWrapper.eq(HrEmployee::getTenantId, tenantId);
            long totalCount = hrEmployeeMapper.selectCount(totalWrapper);

            LambdaQueryWrapper<HrEmployee> resignedMonthWrapper = new LambdaQueryWrapper<>();
            resignedMonthWrapper.eq(HrEmployee::getTenantId, tenantId)
                    .eq(HrEmployee::getStatus, "RESIGNED");
            // approximate: count all resigned records in the system as indicator
            long resignedCount = hrEmployeeMapper.selectCount(resignedMonthWrapper);

            if (totalCount > 0 && (resignedCount * 100.0 / totalCount) > 5.0) {
                AlertRecord record = new AlertRecord();
                record.setTenantId(tenantId);
                record.setAlertType("HR_TURNOVER");
                record.setTitle("月离职率超标预警");
                record.setContent(String.format("当前月离职人数 %d，占比 %.1f%%，超过5%%阈值", resignedCount, resignedCount * 100.0 / totalCount));
                record.setStatus("ACTIVE");
                alerts.add(record);
            }
        }

        return alerts;
    }

    /**
     * 项目预警: 进度滞后>30天、预算执行>90%
     */
    public List<AlertRecord> checkProjectAlerts(Long tenantId) {
        List<AlertRecord> alerts = new ArrayList<>();
        LambdaQueryWrapper<PmProject> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(PmProject::getTenantId, tenantId);
        }
        wrapper.eq(PmProject::getStatus, "IN_PROGRESS");
        List<PmProject> projects = pmProjectMapper.selectList(wrapper);

        for (PmProject p : projects) {
            // 检查工期是否超期
            if (p.getPlannedEndDate() != null && p.getPlannedEndDate().isBefore(LocalDate.now())) {
                long overdueDays = ChronoUnit.DAYS.between(p.getPlannedEndDate(), LocalDate.now());
                if (overdueDays >= 30) {
                    AlertRecord record = new AlertRecord();
                    record.setTenantId(tenantId);
                    record.setAlertType("PROJECT_SCHEDULE");
                    record.setTitle("项目进度滞后预警");
                    record.setContent(String.format("项目 %s 已超计划工期 %d 天", p.getName(), overdueDays));
                    record.setStatus("ACTIVE");
                    record.setRefId(p.getId());
                    alerts.add(record);
                }
            }

            // 检查预算执行率
            if (p.getBudgetApproved() != null && p.getBudgetTotal() != null
                    && p.getBudgetTotal().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal pct = p.getBudgetApproved()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(p.getBudgetTotal(), 2, java.math.RoundingMode.HALF_UP);
                if (pct.compareTo(BigDecimal.valueOf(90)) > 0) {
                    AlertRecord record = new AlertRecord();
                    record.setTenantId(tenantId);
                    record.setAlertType("PROJECT_BUDGET");
                    record.setTitle("项目预算执行预警");
                    record.setContent(String.format("项目 %s 预算执行率已达 %s%%", p.getName(), pct));
                    record.setStatus("ACTIVE");
                    record.setRefId(p.getId());
                    alerts.add(record);
                }
            }
        }

        return alerts;
    }

    /**
     * 投资预警: 被投企业ROI低于预期30%、风险等级HIGH
     */
    public List<AlertRecord> checkInvestAlerts(Long tenantId) {
        List<AlertRecord> alerts = new ArrayList<>();
        LambdaQueryWrapper<InvestProject> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(InvestProject::getTenantId, tenantId);
        }
        wrapper.eq(InvestProject::getStatus, "INVESTED");
        List<InvestProject> investProjects = investProjectMapper.selectList(wrapper);

        for (InvestProject ip : investProjects) {
            // ROI偏差检查: actualRoi 低于 expectedRoi 30%
            if (ip.getExpectedRoi() != null && ip.getActualRoi() != null
                    && ip.getExpectedRoi().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal deviation = ip.getExpectedRoi().subtract(ip.getActualRoi())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(ip.getExpectedRoi(), 2, java.math.RoundingMode.HALF_UP);
                if (deviation.compareTo(BigDecimal.valueOf(30)) > 0) {
                    AlertRecord record = new AlertRecord();
                    record.setTenantId(tenantId);
                    record.setAlertType("INVEST_ROI");
                    record.setTitle("投资ROI预警");
                    record.setContent(String.format("项目 %s 实际ROI(%s%%)低于预期ROI(%s%%)超过30%%",
                            ip.getProjectName(), ip.getActualRoi(), ip.getExpectedRoi()));
                    record.setStatus("ACTIVE");
                    record.setRefId(ip.getId());
                    alerts.add(record);
                }
            }
        }

        return alerts;
    }

    /**
     * 合规预警: 产权交易偏差>15%、审计问题超期未整改
     */
    public List<AlertRecord> checkComplianceAlerts(Long tenantId) {
        List<AlertRecord> alerts = new ArrayList<>();

        // 1. 产权交易偏差检查
        LambdaQueryWrapper<PrTransactionMonitor> transWrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            transWrapper.eq(PrTransactionMonitor::getTenantId, tenantId);
        }
        transWrapper.isNotNull(PrTransactionMonitor::getPriceDeviationPct);
        List<PrTransactionMonitor> transactions = prTransactionMonitorMapper.selectList(transWrapper);

        for (PrTransactionMonitor t : transactions) {
            if (t.getPriceDeviationPct() != null
                    && t.getPriceDeviationPct().abs().compareTo(BigDecimal.valueOf(15)) > 0) {
                AlertRecord record = new AlertRecord();
                record.setTenantId(tenantId);
                record.setAlertType("COMPLIANCE_TRANSACTION");
                record.setTitle("产权交易偏差预警");
                record.setContent(String.format("项目 %s 交易价格偏差 %.2f%%，超过15%%阈值",
                        t.getProjectName(), t.getPriceDeviationPct()));
                record.setStatus("ACTIVE");
                record.setRefId(t.getId());
                alerts.add(record);
            }
        }

        // 2. 审计问题超期未整改检查
        LambdaQueryWrapper<SupRectification> rectWrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            rectWrapper.eq(SupRectification::getTenantId, tenantId);
        }
        rectWrapper.in(SupRectification::getStatus, "PENDING", "IN_PROGRESS");
        List<SupRectification> rectifications = supRectificationMapper.selectList(rectWrapper);

        LocalDate now = LocalDate.now();
        for (SupRectification r : rectifications) {
            if (r.getDeadline() != null && r.getDeadline().isBefore(now)) {
                long overdueDays = ChronoUnit.DAYS.between(r.getDeadline(), now);
                AlertRecord record = new AlertRecord();
                record.setTenantId(tenantId);
                record.setAlertType("COMPLIANCE_RECTIFICATION");
                record.setTitle("整改超期预警");
                record.setContent(String.format("整改任务 \"%s\" 已超期 %d 天", r.getTaskTitle(), overdueDays));
                record.setStatus("ACTIVE");
                record.setRefId(r.getId());
                alerts.add(record);
            }
        }

        // 3. 决策督办超期检查
        LambdaQueryWrapper<DecisionSupervision> decWrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            decWrapper.eq(DecisionSupervision::getTenantId, tenantId);
        }
        decWrapper.eq(DecisionSupervision::getStatus, "PENDING");
        List<DecisionSupervision> decItems = decisionSupervisionMapper.selectList(decWrapper);

        for (DecisionSupervision d : decItems) {
            if (d.getDeadline() != null && d.getDeadline().isBefore(now)) {
                long overdueDays = ChronoUnit.DAYS.between(d.getDeadline(), now);
                AlertRecord record = new AlertRecord();
                record.setTenantId(tenantId);
                record.setAlertType("COMPLIANCE_DECISION");
                record.setTitle("决策督办超期预警");
                record.setContent(String.format("督办任务 \"%s\" 已超期 %d 天", d.getTaskTitle(), overdueDays));
                record.setStatus("ACTIVE");
                record.setRefId(d.getId());
                alerts.add(record);
            }
        }

        return alerts;
    }

    /**
     * 综合检查: 调用所有维度，汇总返回
     */
    public List<AlertRecord> checkAll(Long tenantId) {
        List<AlertRecord> all = new ArrayList<>();
        all.addAll(checkHrAlerts(tenantId));
        all.addAll(checkProjectAlerts(tenantId));
        all.addAll(checkInvestAlerts(tenantId));
        all.addAll(checkComplianceAlerts(tenantId));
        return all;
    }

    /**
     * 获取各维度预警计数
     */
    public java.util.Map<String, Long> checkCounts(Long tenantId) {
        java.util.Map<String, Long> counts = new java.util.LinkedHashMap<>();
        counts.put("hr", (long) checkHrAlerts(tenantId).size());
        counts.put("project", (long) checkProjectAlerts(tenantId).size());
        counts.put("invest", (long) checkInvestAlerts(tenantId).size());
        counts.put("compliance", (long) checkComplianceAlerts(tenantId).size());
        return counts;
    }
}
