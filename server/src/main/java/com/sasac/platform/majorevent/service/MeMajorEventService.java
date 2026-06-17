package com.sasac.platform.majorevent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.majorevent.dto.EventDTO;
import com.sasac.platform.majorevent.dto.GuaranteeDTO;
import com.sasac.platform.majorevent.dto.LawsuitDTO;
import com.sasac.platform.majorevent.entity.MeEvent;
import com.sasac.platform.majorevent.entity.MeGuarantee;
import com.sasac.platform.majorevent.entity.MeLawsuit;
import com.sasac.platform.majorevent.mapper.MeEventMapper;
import com.sasac.platform.majorevent.mapper.MeGuaranteeMapper;
import com.sasac.platform.majorevent.mapper.MeLawsuitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for major event management business logic.
 * <p>
 * Manages major events, lawsuits, and guarantees across the organization.
 */
@Service
@RequiredArgsConstructor
public class MeMajorEventService {

    private final MeEventMapper meEventMapper;
    private final MeLawsuitMapper meLawsuitMapper;
    private final MeGuaranteeMapper meGuaranteeMapper;

    // ===== Events =====

    /**
     * Reports a new major event with an auto-generated event number.
     *
     * @param dto the event details
     * @return the created event
     */
    @Transactional
    public MeEvent report(EventDTO dto) {
        MeEvent event = new MeEvent();
        event.setTenantId(dto.getTenantId());
        event.setOrgId(dto.getOrgId());
        // Auto-generate event number: ZDSX-{yyyyMMddHHmmss}-{random4}
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        int rand = (int) (Math.random() * 9000) + 1000;
        event.setEventNo("ZDSX-" + ts + "-" + rand);
        event.setEventType(dto.getEventType());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setImpactAssessment(dto.getImpactAssessment());
        event.setHandlingPlan(dto.getHandlingPlan());
        event.setStatus("REPORTED");
        event.setReportedAt(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        meEventMapper.insert(event);
        return event;
    }

    /**
     * Updates an existing major event.
     *
     * @param id  the event ID
     * @param dto the updated event details
     * @return the updated event
     */
    @Transactional
    public MeEvent updateEvent(Long id, EventDTO dto) {
        MeEvent existing = meEventMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("重大事件不存在");
        }
        existing.setOrgId(dto.getOrgId());
        existing.setEventType(dto.getEventType());
        existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getImpactAssessment() != null) {
            existing.setImpactAssessment(dto.getImpactAssessment());
        }
        if (dto.getHandlingPlan() != null) {
            existing.setHandlingPlan(dto.getHandlingPlan());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        meEventMapper.updateById(existing);
        return meEventMapper.selectById(id);
    }

    /**
     * Retrieves a major event by its ID.
     *
     * @param id the event ID
     * @return the event
     * @throws BusinessException if not found
     */
    public MeEvent getEvent(Long id) {
        MeEvent event = meEventMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("重大事件不存在");
        }
        return event;
    }

    /**
     * Lists events filtered by tenant, event type, and status.
     *
     * @param tenantId  the tenant ID filter (optional)
     * @param eventType the event type filter (optional)
     * @param status    the status filter (optional)
     * @return list of matching events ordered by creation time descending
     */
    public List<MeEvent> listEvents(Long tenantId, String eventType, String status) {
        LambdaQueryWrapper<MeEvent> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(MeEvent::getTenantId, tenantId);
        }
        if (eventType != null && !eventType.isBlank()) {
            wrapper.eq(MeEvent::getEventType, eventType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(MeEvent::getStatus, status);
        }
        wrapper.orderByDesc(MeEvent::getCreatedAt);
        return meEventMapper.selectList(wrapper);
    }

    /**
     * Approves an event, changing its status to APPROVED.
     *
     * @param id the event ID
     * @return the updated event
     */
    @Transactional
    public MeEvent approve(Long id) {
        MeEvent event = getEvent(id);
        event.setStatus("APPROVED");
        event.setUpdatedAt(LocalDateTime.now());
        meEventMapper.updateById(event);
        return meEventMapper.selectById(id);
    }

    /**
     * Tracks progress by appending a timestamped note to the handling plan.
     *
     * @param id       the event ID
     * @param progress the progress note to append
     * @return the updated event
     */
    @Transactional
    public MeEvent track(Long id, String progress) {
        MeEvent event = getEvent(id);
        String ts = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        String note = "\n[" + ts + "] " + progress;
        String existingPlan = event.getHandlingPlan();
        event.setHandlingPlan(existingPlan != null ? existingPlan + note : note);
        event.setUpdatedAt(LocalDateTime.now());
        meEventMapper.updateById(event);
        return meEventMapper.selectById(id);
    }

    /**
     * Resolves an event, setting its status to RESOLVED.
     *
     * @param id the event ID
     * @return the updated event
     */
    @Transactional
    public MeEvent resolve(Long id) {
        MeEvent event = getEvent(id);
        event.setStatus("RESOLVED");
        event.setResolvedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        meEventMapper.updateById(event);
        return meEventMapper.selectById(id);
    }

    /**
     * Deletes a major event by its ID.
     *
     * @param id the event ID
     */
    @Transactional
    public void deleteEvent(Long id) {
        MeEvent existing = meEventMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("重大事件不存在");
        }
        meEventMapper.deleteById(id);
    }

    // ===== Lawsuits =====

    /**
     * Records a new lawsuit associated with an event.
     *
     * @param dto the lawsuit details
     * @return the created lawsuit
     */
    @Transactional
    public MeLawsuit recordLawsuit(LawsuitDTO dto) {
        MeLawsuit lawsuit = new MeLawsuit();
        lawsuit.setTenantId(dto.getTenantId());
        lawsuit.setEventId(dto.getEventId());
        lawsuit.setCaseNo(dto.getCaseNo());
        lawsuit.setCourt(dto.getCourt());
        lawsuit.setPlaintiff(dto.getPlaintiff());
        lawsuit.setDefendant(dto.getDefendant());
        lawsuit.setClaimAmount(dto.getClaimAmount());
        lawsuit.setJudgmentAmount(dto.getJudgmentAmount());
        lawsuit.setLawFirm(dto.getLawFirm());
        lawsuit.setAttorney(dto.getAttorney());
        lawsuit.setTrialProgress(dto.getTrialProgress());
        lawsuit.setJudgmentDate(dto.getJudgmentDate());
        lawsuit.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        lawsuit.setCreatedAt(LocalDateTime.now());
        lawsuit.setUpdatedAt(LocalDateTime.now());
        meLawsuitMapper.insert(lawsuit);
        return lawsuit;
    }

    /**
     * Updates an existing lawsuit record.
     *
     * @param id  the lawsuit ID
     * @param dto the updated lawsuit details
     * @return the updated lawsuit
     */
    @Transactional
    public MeLawsuit updateLawsuit(Long id, LawsuitDTO dto) {
        MeLawsuit existing = meLawsuitMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("诉讼记录不存在");
        }
        if (dto.getEventId() != null) {
            existing.setEventId(dto.getEventId());
        }
        if (dto.getCaseNo() != null) {
            existing.setCaseNo(dto.getCaseNo());
        }
        if (dto.getCourt() != null) {
            existing.setCourt(dto.getCourt());
        }
        if (dto.getPlaintiff() != null) {
            existing.setPlaintiff(dto.getPlaintiff());
        }
        if (dto.getDefendant() != null) {
            existing.setDefendant(dto.getDefendant());
        }
        if (dto.getClaimAmount() != null) {
            existing.setClaimAmount(dto.getClaimAmount());
        }
        if (dto.getJudgmentAmount() != null) {
            existing.setJudgmentAmount(dto.getJudgmentAmount());
        }
        if (dto.getLawFirm() != null) {
            existing.setLawFirm(dto.getLawFirm());
        }
        if (dto.getAttorney() != null) {
            existing.setAttorney(dto.getAttorney());
        }
        if (dto.getTrialProgress() != null) {
            existing.setTrialProgress(dto.getTrialProgress());
        }
        if (dto.getJudgmentDate() != null) {
            existing.setJudgmentDate(dto.getJudgmentDate());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        meLawsuitMapper.updateById(existing);
        return meLawsuitMapper.selectById(id);
    }

    /**
     * Updates the status and trial progress of a lawsuit.
     *
     * @param id       the lawsuit ID
     * @param status   the new status
     * @param progress the trial progress note
     * @return the updated lawsuit
     */
    @Transactional
    public MeLawsuit updateLawsuitStatus(Long id, String status, String progress) {
        MeLawsuit existing = meLawsuitMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("诉讼记录不存在");
        }
        existing.setStatus(status);
        existing.setTrialProgress(progress);
        existing.setUpdatedAt(LocalDateTime.now());
        meLawsuitMapper.updateById(existing);
        return meLawsuitMapper.selectById(id);
    }

    /**
     * Lists lawsuits filtered by tenant, event, and status.
     *
     * @param tenantId the tenant ID filter (optional)
     * @param eventId  the event ID filter (optional)
     * @param status   the status filter (optional)
     * @return list of matching lawsuits ordered by creation time descending
     */
    public List<MeLawsuit> listLawsuits(Long tenantId, Long eventId, String status) {
        LambdaQueryWrapper<MeLawsuit> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(MeLawsuit::getTenantId, tenantId);
        }
        if (eventId != null) {
            wrapper.eq(MeLawsuit::getEventId, eventId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(MeLawsuit::getStatus, status);
        }
        wrapper.orderByDesc(MeLawsuit::getCreatedAt);
        return meLawsuitMapper.selectList(wrapper);
    }

    /**
     * Retrieves a lawsuit by its ID.
     *
     * @param id the lawsuit ID
     * @return the lawsuit
     * @throws BusinessException if not found
     */
    public MeLawsuit getLawsuit(Long id) {
        MeLawsuit lawsuit = meLawsuitMapper.selectById(id);
        if (lawsuit == null) {
            throw new BusinessException("诉讼记录不存在");
        }
        return lawsuit;
    }

    /**
     * Deletes a lawsuit by its ID.
     *
     * @param id the lawsuit ID
     */
    @Transactional
    public void deleteLawsuit(Long id) {
        MeLawsuit existing = meLawsuitMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("诉讼记录不存在");
        }
        meLawsuitMapper.deleteById(id);
    }

    // ===== Guarantees =====

    /**
     * Records a new guarantee associated with an event.
     *
     * @param dto the guarantee details
     * @return the created guarantee
     */
    @Transactional
    public MeGuarantee recordGuarantee(GuaranteeDTO dto) {
        MeGuarantee guarantee = new MeGuarantee();
        guarantee.setTenantId(dto.getTenantId());
        guarantee.setEventId(dto.getEventId());
        guarantee.setGuaranteeType(dto.getGuaranteeType());
        guarantee.setBeneficiary(dto.getBeneficiary());
        guarantee.setGuaranteeAmount(dto.getGuaranteeAmount());
        guarantee.setGuaranteePeriodStart(dto.getGuaranteePeriodStart());
        guarantee.setGuaranteePeriodEnd(dto.getGuaranteePeriodEnd());
        guarantee.setCounterGuarantee(dto.getCounterGuarantee());
        guarantee.setRiskLevel(dto.getRiskLevel());
        guarantee.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        guarantee.setCreatedAt(LocalDateTime.now());
        guarantee.setUpdatedAt(LocalDateTime.now());
        meGuaranteeMapper.insert(guarantee);
        return guarantee;
    }

    /**
     * Updates an existing guarantee record.
     *
     * @param id  the guarantee ID
     * @param dto the updated guarantee details
     * @return the updated guarantee
     */
    @Transactional
    public MeGuarantee updateGuarantee(Long id, GuaranteeDTO dto) {
        MeGuarantee existing = meGuaranteeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("担保记录不存在");
        }
        if (dto.getEventId() != null) {
            existing.setEventId(dto.getEventId());
        }
        if (dto.getGuaranteeType() != null) {
            existing.setGuaranteeType(dto.getGuaranteeType());
        }
        if (dto.getBeneficiary() != null) {
            existing.setBeneficiary(dto.getBeneficiary());
        }
        if (dto.getGuaranteeAmount() != null) {
            existing.setGuaranteeAmount(dto.getGuaranteeAmount());
        }
        if (dto.getGuaranteePeriodStart() != null) {
            existing.setGuaranteePeriodStart(dto.getGuaranteePeriodStart());
        }
        if (dto.getGuaranteePeriodEnd() != null) {
            existing.setGuaranteePeriodEnd(dto.getGuaranteePeriodEnd());
        }
        if (dto.getCounterGuarantee() != null) {
            existing.setCounterGuarantee(dto.getCounterGuarantee());
        }
        if (dto.getRiskLevel() != null) {
            existing.setRiskLevel(dto.getRiskLevel());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        meGuaranteeMapper.updateById(existing);
        return meGuaranteeMapper.selectById(id);
    }

    /**
     * Lists guarantees filtered by tenant, event, and risk level.
     *
     * @param tenantId the tenant ID filter (optional)
     * @param eventId  the event ID filter (optional)
     * @param riskLevel the risk level filter (optional)
     * @return list of matching guarantees ordered by creation time descending
     */
    public List<MeGuarantee> listGuarantees(Long tenantId, Long eventId, String riskLevel) {
        LambdaQueryWrapper<MeGuarantee> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(MeGuarantee::getTenantId, tenantId);
        }
        if (eventId != null) {
            wrapper.eq(MeGuarantee::getEventId, eventId);
        }
        if (riskLevel != null && !riskLevel.isBlank()) {
            wrapper.eq(MeGuarantee::getRiskLevel, riskLevel);
        }
        wrapper.orderByDesc(MeGuarantee::getCreatedAt);
        return meGuaranteeMapper.selectList(wrapper);
    }

    /**
     * Retrieves a guarantee by its ID.
     *
     * @param id the guarantee ID
     * @return the guarantee
     * @throws BusinessException if not found
     */
    public MeGuarantee getGuarantee(Long id) {
        MeGuarantee guarantee = meGuaranteeMapper.selectById(id);
        if (guarantee == null) {
            throw new BusinessException("担保记录不存在");
        }
        return guarantee;
    }

    /**
     * Finds guarantees that are about to expire within the specified number of days.
     *
     * @param tenantId the tenant ID
     * @param days     the number of days from now to check
     * @return list of guarantees expiring within the period
     */
    public List<MeGuarantee> findExpiringGuarantees(Long tenantId, int days) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);
        LambdaQueryWrapper<MeGuarantee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeGuarantee::getTenantId, tenantId);
        wrapper.eq(MeGuarantee::getStatus, "ACTIVE");
        wrapper.ge(MeGuarantee::getGuaranteePeriodEnd, now);
        wrapper.le(MeGuarantee::getGuaranteePeriodEnd, endDate);
        wrapper.orderByAsc(MeGuarantee::getGuaranteePeriodEnd);
        return meGuaranteeMapper.selectList(wrapper);
    }

    /**
     * Deletes a guarantee by its ID.
     *
     * @param id the guarantee ID
     */
    @Transactional
    public void deleteGuarantee(Long id) {
        MeGuarantee existing = meGuaranteeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("担保记录不存在");
        }
        meGuaranteeMapper.deleteById(id);
    }
}
