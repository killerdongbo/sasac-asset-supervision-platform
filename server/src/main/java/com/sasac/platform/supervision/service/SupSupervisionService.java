package com.sasac.platform.supervision.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.dto.AuditPlanDTO;
import com.sasac.platform.supervision.dto.CaseDTO;
import com.sasac.platform.supervision.dto.FindingDTO;
import com.sasac.platform.supervision.dto.RectificationDTO;
import com.sasac.platform.supervision.entity.SupAuditFinding;
import com.sasac.platform.supervision.entity.SupAuditPlan;
import com.sasac.platform.supervision.entity.SupRectification;
import com.sasac.platform.supervision.entity.SupViolationCase;
import com.sasac.platform.supervision.mapper.SupAuditFindingMapper;
import com.sasac.platform.supervision.mapper.SupAuditPlanMapper;
import com.sasac.platform.supervision.mapper.SupRectificationMapper;
import com.sasac.platform.supervision.mapper.SupViolationCaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for supervision and accountability business logic.
 * <p>
 * Manages audit plans, findings, rectification tasks, and violation cases.
 */
@Service
@RequiredArgsConstructor
public class SupSupervisionService {

    private final SupAuditPlanMapper supAuditPlanMapper;
    private final SupAuditFindingMapper supAuditFindingMapper;
    private final SupRectificationMapper supRectificationMapper;
    private final SupViolationCaseMapper supViolationCaseMapper;

    // ===== Audit Plans =====

    @Transactional
    public SupAuditPlan createAuditPlan(AuditPlanDTO dto) {
        SupAuditPlan plan = new SupAuditPlan();
        plan.setTenantId(dto.getTenantId());
        plan.setOrgId(dto.getOrgId());
        plan.setPlanYear(dto.getPlanYear());
        plan.setPlanName(dto.getPlanName());
        plan.setAuditScope(dto.getAuditScope());
        plan.setAuditTeam(dto.getAuditTeam());
        plan.setStatus(dto.getStatus() != null ? dto.getStatus() : "DRAFT");
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        supAuditPlanMapper.insert(plan);
        return plan;
    }

    @Transactional
    public SupAuditPlan updateAuditPlan(Long id, AuditPlanDTO dto) {
        SupAuditPlan existing = supAuditPlanMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("审计计划不存在");
        }
        existing.setPlanYear(dto.getPlanYear());
        existing.setPlanName(dto.getPlanName());
        existing.setAuditScope(dto.getAuditScope());
        existing.setAuditTeam(dto.getAuditTeam());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        supAuditPlanMapper.updateById(existing);
        return supAuditPlanMapper.selectById(id);
    }

    public List<SupAuditPlan> listAuditPlans(Long tenantId, Integer planYear) {
        LambdaQueryWrapper<SupAuditPlan> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SupAuditPlan::getTenantId, tenantId);
        }
        if (planYear != null) {
            wrapper.eq(SupAuditPlan::getPlanYear, planYear);
        }
        wrapper.orderByDesc(SupAuditPlan::getPlanYear);
        return supAuditPlanMapper.selectList(wrapper);
    }

    public SupAuditPlan getAuditPlan(Long id) {
        SupAuditPlan plan = supAuditPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("审计计划不存在");
        }
        return plan;
    }

    @Transactional
    public void deleteAuditPlan(Long id) {
        SupAuditPlan existing = supAuditPlanMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("审计计划不存在");
        }
        supAuditPlanMapper.deleteById(id);
    }

    // ===== Audit Findings =====

    @Transactional
    public SupAuditFinding recordFinding(FindingDTO dto) {
        SupAuditFinding finding = new SupAuditFinding();
        finding.setTenantId(dto.getTenantId());
        finding.setPlanId(dto.getPlanId());
        // Auto-generate finding number: FX-{timestamp}
        finding.setFindingNo("FX-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        finding.setTitle(dto.getTitle());
        finding.setSeverity(dto.getSeverity());
        finding.setDescription(dto.getDescription());
        finding.setEvidenceIds(dto.getEvidenceIds());
        finding.setStatus("OPEN");
        finding.setCreatedAt(LocalDateTime.now());
        finding.setUpdatedAt(LocalDateTime.now());
        supAuditFindingMapper.insert(finding);
        return finding;
    }

    public List<SupAuditFinding> listFindings(Long tenantId, Long planId, String severity) {
        LambdaQueryWrapper<SupAuditFinding> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SupAuditFinding::getTenantId, tenantId);
        }
        if (planId != null) {
            wrapper.eq(SupAuditFinding::getPlanId, planId);
        }
        if (severity != null && !severity.isBlank()) {
            wrapper.eq(SupAuditFinding::getSeverity, severity);
        }
        wrapper.orderByDesc(SupAuditFinding::getCreatedAt);
        return supAuditFindingMapper.selectList(wrapper);
    }

    // ===== Rectifications =====

    @Transactional
    public SupRectification assignRectification(Long findingId, RectificationDTO dto) {
        SupAuditFinding finding = supAuditFindingMapper.selectById(findingId);
        if (finding == null) {
            throw new BusinessException("审计发现不存在");
        }
        SupRectification rect = new SupRectification();
        rect.setTenantId(dto.getTenantId());
        rect.setFindingId(findingId);
        rect.setTaskTitle(dto.getTaskTitle());
        rect.setAssigneeId(dto.getAssigneeId());
        rect.setAssigneeName(dto.getAssigneeName());
        rect.setDeadline(dto.getDeadline());
        rect.setRectificationMeasure(dto.getRectificationMeasure());
        rect.setStatus("PENDING");
        rect.setCreatedAt(LocalDateTime.now());
        rect.setUpdatedAt(LocalDateTime.now());
        supRectificationMapper.insert(rect);

        // Update finding status to RECTIFYING
        finding.setStatus("RECTIFYING");
        finding.setUpdatedAt(LocalDateTime.now());
        supAuditFindingMapper.updateById(finding);

        return rect;
    }

    @Transactional
    public SupRectification verifyRectification(Long id, String result) {
        SupRectification rect = supRectificationMapper.selectById(id);
        if (rect == null) {
            throw new BusinessException("整改任务不存在");
        }
        rect.setResultVerification(result);
        rect.setStatus("COMPLETED");
        rect.setCompletedAt(LocalDateTime.now());
        rect.setUpdatedAt(LocalDateTime.now());
        supRectificationMapper.updateById(rect);

        // Update associated finding status to VERIFIED
        if (rect.getFindingId() != null) {
            SupAuditFinding finding = supAuditFindingMapper.selectById(rect.getFindingId());
            if (finding != null) {
                finding.setStatus("VERIFIED");
                finding.setUpdatedAt(LocalDateTime.now());
                supAuditFindingMapper.updateById(finding);
            }
        }

        return supRectificationMapper.selectById(id);
    }

    public List<SupRectification> listRectifications(Long tenantId, Long findingId, String status) {
        LambdaQueryWrapper<SupRectification> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SupRectification::getTenantId, tenantId);
        }
        if (findingId != null) {
            wrapper.eq(SupRectification::getFindingId, findingId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(SupRectification::getStatus, status);
        }
        wrapper.orderByDesc(SupRectification::getCreatedAt);
        return supRectificationMapper.selectList(wrapper);
    }

    /**
     * Finds all PENDING rectifications past their deadline and marks them as ESCALATED.
     *
     * @param tenantId the tenant ID to check
     * @return list of escalated rectifications
     */
    @Transactional
    public List<SupRectification> checkOverdueRectifications(Long tenantId) {
        LambdaQueryWrapper<SupRectification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupRectification::getTenantId, tenantId);
        wrapper.eq(SupRectification::getStatus, "PENDING");
        wrapper.lt(SupRectification::getDeadline, LocalDate.now());
        List<SupRectification> overdue = supRectificationMapper.selectList(wrapper);
        for (SupRectification rect : overdue) {
            rect.setStatus("ESCALATED");
            rect.setUpdatedAt(LocalDateTime.now());
            supRectificationMapper.updateById(rect);
        }
        return overdue;
    }

    // ===== Violation Cases =====

    @Transactional
    public SupViolationCase openCase(CaseDTO dto) {
        SupViolationCase violationCase = new SupViolationCase();
        violationCase.setTenantId(dto.getTenantId());
        // Auto-generate case number: WJ-{timestamp}
        violationCase.setCaseNo("WJ-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        violationCase.setCaseTitle(dto.getCaseTitle());
        violationCase.setViolationType(dto.getViolationType());
        violationCase.setSuspectId(dto.getSuspectId());
        violationCase.setSuspectName(dto.getSuspectName());
        violationCase.setStatus("INVESTIGATING");
        violationCase.setCreatedAt(LocalDateTime.now());
        violationCase.setUpdatedAt(LocalDateTime.now());
        supViolationCaseMapper.insert(violationCase);
        return violationCase;
    }

    @Transactional
    public SupViolationCase investigate(Long id, String result, BigDecimal assetLoss) {
        SupViolationCase violationCase = supViolationCaseMapper.selectById(id);
        if (violationCase == null) {
            throw new BusinessException("案件不存在");
        }
        violationCase.setInvestigationResult(result);
        if (assetLoss != null) {
            violationCase.setAssetLoss(assetLoss);
        }
        violationCase.setUpdatedAt(LocalDateTime.now());
        supViolationCaseMapper.updateById(violationCase);
        return supViolationCaseMapper.selectById(id);
    }

    @Transactional
    public SupViolationCase decidePunishment(Long id, String decision) {
        SupViolationCase violationCase = supViolationCaseMapper.selectById(id);
        if (violationCase == null) {
            throw new BusinessException("案件不存在");
        }
        violationCase.setPunishmentDecision(decision);
        violationCase.setStatus("RESOLVED");
        violationCase.setUpdatedAt(LocalDateTime.now());
        supViolationCaseMapper.updateById(violationCase);
        return supViolationCaseMapper.selectById(id);
    }

    public List<SupViolationCase> listCases(Long tenantId, String status, String violationType) {
        LambdaQueryWrapper<SupViolationCase> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SupViolationCase::getTenantId, tenantId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(SupViolationCase::getStatus, status);
        }
        if (violationType != null && !violationType.isBlank()) {
            wrapper.eq(SupViolationCase::getViolationType, violationType);
        }
        wrapper.orderByDesc(SupViolationCase::getCreatedAt);
        return supViolationCaseMapper.selectList(wrapper);
    }

    public SupViolationCase getCase(Long id) {
        SupViolationCase violationCase = supViolationCaseMapper.selectById(id);
        if (violationCase == null) {
            throw new BusinessException("案件不存在");
        }
        return violationCase;
    }
}
