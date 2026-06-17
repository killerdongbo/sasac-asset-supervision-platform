package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.entity.HrTraining;
import com.sasac.platform.hr.mapper.HrTrainingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for training course management (CRUD).
 */
@Service
@RequiredArgsConstructor
public class HrTrainingService {

    private final HrTrainingMapper hrTrainingMapper;

    /**
     * Creates a new training record.
     *
     * @param training the training entity
     * @return the created training record
     */
    @Transactional
    public HrTraining create(HrTraining training) {
        hrTrainingMapper.insert(training);
        return training;
    }

    /**
     * Queries training records with pagination.
     *
     * @param page     page number
     * @param limit    page size
     * @param tenantId tenant ID filter (optional)
     * @return paginated training records
     */
    public Page<HrTraining> query(int page, int limit, Long tenantId) {
        LambdaQueryWrapper<HrTraining> wrapper = new LambdaQueryWrapper<>();

        if (tenantId != null) {
            wrapper.eq(HrTraining::getTenantId, tenantId);
        }

        wrapper.orderByDesc(HrTraining::getId);

        return hrTrainingMapper.selectPage(
                new Page<>(page, limit),
                wrapper
        );
    }

    /**
     * Retrieves a training record by ID.
     *
     * @param id the training record ID
     * @return the training record
     * @throws BusinessException if not found
     */
    public HrTraining getById(Long id) {
        HrTraining training = hrTrainingMapper.selectById(id);
        if (training == null) {
            throw new BusinessException("培训记录不存在");
        }
        return training;
    }

    /**
     * Updates an existing training record.
     *
     * @param id       the record ID to update
     * @param training the updated fields
     * @return the updated training record
     */
    @Transactional
    public HrTraining update(Long id, HrTraining training) {
        HrTraining existing = getById(id);
        existing.setCourseName(training.getCourseName());
        existing.setTrainer(training.getTrainer());
        existing.setTrainingDate(training.getTrainingDate());
        existing.setDurationHours(training.getDurationHours());
        existing.setAttendees(training.getAttendees());
        existing.setEvaluationSummary(training.getEvaluationSummary());
        existing.setAttachmentIds(training.getAttachmentIds());

        hrTrainingMapper.updateById(existing);
        return hrTrainingMapper.selectById(id);
    }

    /**
     * Deletes a training record by ID.
     *
     * @param id the record ID to delete
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        hrTrainingMapper.deleteById(id);
    }
}
