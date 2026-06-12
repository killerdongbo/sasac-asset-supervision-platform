package com.sasac.platform.supervision.alert.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.entity.AlertRule;
import com.sasac.platform.supervision.alert.mapper.AlertRecordMapper;
import com.sasac.platform.supervision.alert.mapper.AlertRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for alert rule management and alert record lifecycle.
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRuleMapper alertRuleMapper;
    private final AlertRecordMapper alertRecordMapper;

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
        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<AlertRule>()
                .eq(AlertRule::getTenantId, tenantId);
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
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<AlertRecord>()
                .eq(AlertRecord::getTenantId, tenantId);
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
}
