package com.sasac.platform.decision.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.decision.dto.DecisionItemCreateDTO;
import com.sasac.platform.decision.dto.MeetingCreateDTO;
import com.sasac.platform.decision.dto.ResolutionCreateDTO;
import com.sasac.platform.decision.dto.SupervisionCreateDTO;
import com.sasac.platform.decision.entity.DecisionItem;
import com.sasac.platform.decision.entity.DecisionMeeting;
import com.sasac.platform.decision.entity.DecisionResolution;
import com.sasac.platform.decision.entity.DecisionSupervision;
import com.sasac.platform.decision.mapper.DecisionItemMapper;
import com.sasac.platform.decision.mapper.DecisionMeetingMapper;
import com.sasac.platform.decision.mapper.DecisionResolutionMapper;
import com.sasac.platform.decision.mapper.DecisionSupervisionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for 三重一大 decision management operations.
 */
@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionItemMapper itemMapper;
    private final DecisionMeetingMapper meetingMapper;
    private final DecisionResolutionMapper resolutionMapper;
    private final DecisionSupervisionMapper supervisionMapper;

    /**
     * Submits a new decision item.
     *
     * @param dto the item creation data
     * @return the created item
     */
    @Transactional
    public DecisionItem submitItem(DecisionItemCreateDTO dto) {
        DecisionItem item = new DecisionItem();
        BeanUtils.copyProperties(dto, item);
        item.setStatus("DRAFT");
        item.setItemNo("DJ-" + System.currentTimeMillis());
        itemMapper.insert(item);
        return item;
    }

    /**
     * Queries decision items with filters and pagination.
     *
     * @param tenantId the tenant ID
     * @param itemType optional item type filter
     * @param status   optional status filter
     * @param page     page number
     * @param limit    page size
     * @return paginated result
     */
    public Page<DecisionItem> queryItems(Long tenantId, String itemType, String status, int page, int limit) {
        LambdaQueryWrapper<DecisionItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DecisionItem::getTenantId, tenantId);
        if (itemType != null && !itemType.isBlank()) {
            wrapper.eq(DecisionItem::getItemType, itemType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(DecisionItem::getStatus, status);
        }
        wrapper.orderByDesc(DecisionItem::getId);
        return itemMapper.selectPage(new Page<>(page, limit), wrapper);
    }

    /**
     * Creates a new decision meeting.
     *
     * @param dto the meeting creation data
     * @return the created meeting
     */
    @Transactional
    public DecisionMeeting createMeeting(MeetingCreateDTO dto) {
        DecisionMeeting meeting = new DecisionMeeting();
        BeanUtils.copyProperties(dto, meeting);
        meeting.setStatus("SCHEDULED");
        meeting.setMeetingNo("MT-" + System.currentTimeMillis());
        meetingMapper.insert(meeting);
        return meeting;
    }

    /**
     * Creates a resolution for a specific meeting.
     *
     * @param meetingId the meeting ID
     * @param dto       the resolution creation data
     * @return the created resolution
     * @throws BusinessException if the meeting does not exist
     */
    @Transactional
    public DecisionResolution createResolution(Long meetingId, ResolutionCreateDTO dto) {
        DecisionMeeting meeting = meetingMapper.selectById(meetingId);
        if (meeting == null) {
            throw new BusinessException("会议不存在");
        }
        DecisionResolution resolution = new DecisionResolution();
        BeanUtils.copyProperties(dto, resolution);
        resolution.setMeetingId(meetingId);
        resolution.setTenantId(meeting.getTenantId());
        resolution.setResolutionNo("RES-" + System.currentTimeMillis());
        resolution.setStatus("APPROVED");
        resolutionMapper.insert(resolution);
        return resolution;
    }

    /**
     * Creates a supervision task for a specific resolution.
     *
     * @param resolutionId the resolution ID
     * @param dto          the supervision creation data
     * @return the created supervision task
     */
    @Transactional
    public DecisionSupervision createSupervision(Long resolutionId, SupervisionCreateDTO dto) {
        DecisionResolution resolution = resolutionMapper.selectById(resolutionId);
        if (resolution == null) {
            throw new BusinessException("决议不存在");
        }
        DecisionSupervision sup = new DecisionSupervision();
        BeanUtils.copyProperties(dto, sup);
        sup.setResolutionId(resolutionId);
        sup.setStatus("PENDING");
        supervisionMapper.insert(sup);
        return sup;
    }

    /**
     * Retrieves all pending supervision tasks for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of pending supervisions
     */
    public List<DecisionSupervision> queryPendingSupervisions(Long tenantId) {
        return supervisionMapper.selectList(
                new LambdaQueryWrapper<DecisionSupervision>()
                        .eq(DecisionSupervision::getTenantId, tenantId)
                        .eq(DecisionSupervision::getStatus, "PENDING"));
    }

    /**
     * Updates the progress note of a supervision task.
     *
     * @param id           the supervision task ID
     * @param progressNote the progress note to update
     * @return the updated supervision task
     * @throws BusinessException if the task is not found or already completed
     */
    @Transactional
    public DecisionSupervision updateProgress(Long id, String progressNote) {
        DecisionSupervision sup = supervisionMapper.selectById(id);
        if (sup == null) {
            throw new BusinessException("督办任务不存在");
        }
        sup.setProgressNote(progressNote);
        supervisionMapper.updateById(sup);
        return supervisionMapper.selectById(id);
    }

    /**
     * Marks a supervision task as completed.
     *
     * @param id the supervision task ID
     * @return the completed supervision task
     * @throws BusinessException if the task is not found
     */
    @Transactional
    public DecisionSupervision completeSupervision(Long id) {
        DecisionSupervision sup = supervisionMapper.selectById(id);
        if (sup == null) {
            throw new BusinessException("督办任务不存在");
        }
        sup.setStatus("COMPLETED");
        supervisionMapper.updateById(sup);
        return supervisionMapper.selectById(id);
    }
}
