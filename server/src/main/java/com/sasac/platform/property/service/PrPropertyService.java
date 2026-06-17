package com.sasac.platform.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.property.dto.AssessmentDTO;
import com.sasac.platform.property.dto.ChangeDTO;
import com.sasac.platform.property.dto.RegistrationDTO;
import com.sasac.platform.property.dto.TransactionDTO;
import com.sasac.platform.property.entity.PrAssessment;
import com.sasac.platform.property.entity.PrChange;
import com.sasac.platform.property.entity.PrRegistration;
import com.sasac.platform.property.entity.PrTransactionMonitor;
import com.sasac.platform.property.mapper.PrAssessmentMapper;
import com.sasac.platform.property.mapper.PrChangeMapper;
import com.sasac.platform.property.mapper.PrRegistrationMapper;
import com.sasac.platform.property.mapper.PrTransactionMonitorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for property rights management operations.
 */
@Service
@RequiredArgsConstructor
public class PrPropertyService {

    private final PrRegistrationMapper prRegistrationMapper;
    private final PrChangeMapper prChangeMapper;
    private final PrAssessmentMapper prAssessmentMapper;
    private final PrTransactionMonitorMapper prTransactionMonitorMapper;

    /**
     * Registers a new property right with auto-generated reg_no.
     *
     * @param dto the registration data
     * @return the created registration
     */
    @Transactional
    public PrRegistration register(RegistrationDTO dto) {
        PrRegistration registration = new PrRegistration();
        BeanUtils.copyProperties(dto, registration);

        // Auto-generate reg_no: CQ-{timestamp}
        String regNo = "CQ-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + System.currentTimeMillis() % 100000;
        registration.setRegNo(regNo);
        registration.setStatus("ACTIVE");

        prRegistrationMapper.insert(registration);
        return registration;
    }

    /**
     * Retrieves a registration by ID.
     *
     * @param id the registration ID
     * @return the registration
     * @throws BusinessException if not found
     */
    public PrRegistration getRegistrationById(Long id) {
        PrRegistration registration = prRegistrationMapper.selectById(id);
        if (registration == null) {
            throw new BusinessException("产权登记记录不存在");
        }
        return registration;
    }

    /**
     * Queries registrations with dynamic filters and pagination.
     *
     * @param tenantId the tenant ID
     * @param regType  the registration type filter
     * @param status   the status filter
     * @param keyword  the keyword for search
     * @param page     the page number
     * @param limit    the page size
     * @return paginated registration list
     */
    public Page<PrRegistration> queryRegistrations(Long tenantId, String regType, String status,
                                                    String keyword, int page, int limit) {
        LambdaQueryWrapper<PrRegistration> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(PrRegistration::getTenantId, tenantId);

        if (regType != null && !regType.isBlank()) {
            wrapper.eq(PrRegistration::getRegType, regType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(PrRegistration::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(PrRegistration::getEnterpriseName, keyword)
                    .or()
                    .like(PrRegistration::getRegNo, keyword)
            );
        }

        wrapper.orderByDesc(PrRegistration::getId);

        return prRegistrationMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Records a property right change for a registration.
     *
     * @param regId the registration ID
     * @param dto   the change data
     * @return the created change record
     */
    @Transactional
    public PrChange recordChange(Long regId, ChangeDTO dto) {
        // Verify registration exists
        getRegistrationById(regId);

        PrChange change = new PrChange();
        BeanUtils.copyProperties(dto, change);
        change.setRegistrationId(regId);

        prChangeMapper.insert(change);
        return change;
    }

    /**
     * Creates an assessment filing with auto-calculated deviation percentage.
     *
     * @param dto the assessment data
     * @return the created assessment
     */
    @Transactional
    public PrAssessment assess(AssessmentDTO dto) {
        PrAssessment assessment = new PrAssessment();
        BeanUtils.copyProperties(dto, assessment);

        // Calculate deviation rate |assessed - book| / book * 100%
        if (dto.getBookValue() != null && dto.getAssessedValue() != null
                && dto.getBookValue().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diff = dto.getAssessedValue().subtract(dto.getBookValue()).abs();
            BigDecimal deviation = diff.divide(dto.getBookValue(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            assessment.setDeviationPct(deviation.setScale(2, RoundingMode.HALF_UP));
        } else {
            assessment.setDeviationPct(BigDecimal.ZERO);
        }

        assessment.setApprovalStatus("PENDING");

        prAssessmentMapper.insert(assessment);
        return assessment;
    }

    /**
     * Records a transaction monitoring entry.
     * Automatically marks as abnormal if price deviation > 15%.
     *
     * @param dto the transaction data
     * @return the created transaction monitor record
     */
    @Transactional
    public PrTransactionMonitor monitorTransaction(TransactionDTO dto) {
        PrTransactionMonitor monitor = new PrTransactionMonitor();
        BeanUtils.copyProperties(dto, monitor);

        // Calculate price deviation percentage
        if (dto.getListingPrice() != null && dto.getTransactionPrice() != null
                && dto.getListingPrice().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diff = dto.getTransactionPrice().subtract(dto.getListingPrice()).abs();
            BigDecimal deviation = diff.divide(dto.getListingPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            monitor.setPriceDeviationPct(deviation.setScale(2, RoundingMode.HALF_UP));

            // Mark abnormal if deviation > 15%
            monitor.setIsAbnormal(deviation.compareTo(BigDecimal.valueOf(15)) > 0);
        } else {
            monitor.setPriceDeviationPct(BigDecimal.ZERO);
            monitor.setIsAbnormal(false);
        }

        prTransactionMonitorMapper.insert(monitor);
        return monitor;
    }

    /**
     * Queries transaction monitors with optional abnormal filter.
     *
     * @param tenantId   the tenant ID
     * @param isAbnormal filter for abnormal transactions only
     * @param page       the page number
     * @param limit      the page size
     * @return paginated transaction monitor list
     */
    public Page<PrTransactionMonitor> queryTransactions(Long tenantId, Boolean isAbnormal,
                                                         int page, int limit) {
        LambdaQueryWrapper<PrTransactionMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrTransactionMonitor::getTenantId, tenantId);

        if (isAbnormal != null && isAbnormal) {
            wrapper.eq(PrTransactionMonitor::getIsAbnormal, true);
        }

        wrapper.orderByDesc(PrTransactionMonitor::getId);

        return prTransactionMonitorMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Queries assessments with pagination.
     *
     * @param tenantId the tenant ID
     * @param page     the page number
     * @param limit    the page size
     * @return paginated assessment list
     */
    public Page<PrAssessment> queryAssessments(Long tenantId, int page, int limit) {
        LambdaQueryWrapper<PrAssessment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrAssessment::getTenantId, tenantId);
        wrapper.orderByDesc(PrAssessment::getId);
        return prAssessmentMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Builds a property tree structure by org_id hierarchy.
     * Returns a list of tree nodes with children.
     *
     * @param tenantId the tenant ID
     * @return list of tree node maps
     */
    public List<Map<String, Object>> buildPropertyTree(Long tenantId) {
        LambdaQueryWrapper<PrRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrRegistration::getTenantId, tenantId)
                .eq(PrRegistration::getStatus, "ACTIVE");
        wrapper.orderByAsc(PrRegistration::getOrgId);

        List<PrRegistration> registrations = prRegistrationMapper.selectList(wrapper);

        // Group by org_id
        Map<Long, List<PrRegistration>> grouped = new HashMap<>();
        for (PrRegistration reg : registrations) {
            grouped.computeIfAbsent(reg.getOrgId(), k -> new ArrayList<>()).add(reg);
        }

        // Build tree structure
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map.Entry<Long, List<PrRegistration>> entry : grouped.entrySet()) {
            Map<String, Object> orgNode = new HashMap<>();
            orgNode.put("orgId", entry.getKey());
            orgNode.put("label", "组织-" + entry.getKey());

            List<Map<String, Object>> children = new ArrayList<>();
            for (PrRegistration reg : entry.getValue()) {
                Map<String, Object> regNode = new HashMap<>();
                regNode.put("id", reg.getId());
                regNode.put("label", reg.getEnterpriseName());
                regNode.put("regNo", reg.getRegNo());
                regNode.put("regType", reg.getRegType());
                regNode.put("equityPct", reg.getEquityPct());
                regNode.put("isLeaf", true);
                children.add(regNode);
            }
            orgNode.put("children", children);
            tree.add(orgNode);
        }

        return tree;
    }
}
