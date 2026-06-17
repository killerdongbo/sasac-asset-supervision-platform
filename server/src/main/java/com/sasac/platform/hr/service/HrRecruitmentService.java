package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.entity.HrRecruitment;
import com.sasac.platform.hr.mapper.HrRecruitmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for recruitment position management (CRUD).
 */
@Service
@RequiredArgsConstructor
public class HrRecruitmentService {

    private final HrRecruitmentMapper hrRecruitmentMapper;

    /**
     * Creates a new recruitment record.
     *
     * @param recruitment the recruitment entity
     * @return the created recruitment record
     */
    @Transactional
    public HrRecruitment create(HrRecruitment recruitment) {
        if (recruitment.getStatus() == null) {
            recruitment.setStatus("OPEN");
        }
        hrRecruitmentMapper.insert(recruitment);
        return recruitment;
    }

    /**
     * Queries recruitment records with pagination.
     *
     * @param page     page number
     * @param limit    page size
     * @param tenantId tenant ID filter (optional)
     * @return paginated recruitment records
     */
    public Page<HrRecruitment> query(int page, int limit, Long tenantId) {
        LambdaQueryWrapper<HrRecruitment> wrapper = new LambdaQueryWrapper<>();

        if (tenantId != null) {
            wrapper.eq(HrRecruitment::getTenantId, tenantId);
        }

        wrapper.orderByDesc(HrRecruitment::getId);

        return hrRecruitmentMapper.selectPage(
                new Page<>(page, limit),
                wrapper
        );
    }

    /**
     * Retrieves a recruitment record by ID.
     *
     * @param id the recruitment record ID
     * @return the recruitment record
     * @throws BusinessException if not found
     */
    public HrRecruitment getById(Long id) {
        HrRecruitment recruitment = hrRecruitmentMapper.selectById(id);
        if (recruitment == null) {
            throw new BusinessException("招聘记录不存在");
        }
        return recruitment;
    }

    /**
     * Updates an existing recruitment record.
     *
     * @param id          the record ID to update
     * @param recruitment the updated fields
     * @return the updated recruitment record
     */
    @Transactional
    public HrRecruitment update(Long id, HrRecruitment recruitment) {
        HrRecruitment existing = getById(id);
        existing.setPositionName(recruitment.getPositionName());
        existing.setHeadcount(recruitment.getHeadcount());
        existing.setPipeline(recruitment.getPipeline());
        existing.setCandidates(recruitment.getCandidates());
        existing.setStatus(recruitment.getStatus());
        existing.setOrgId(recruitment.getOrgId());

        hrRecruitmentMapper.updateById(existing);
        return hrRecruitmentMapper.selectById(id);
    }

    /**
     * Deletes a recruitment record by ID.
     *
     * @param id the record ID to delete
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        hrRecruitmentMapper.deleteById(id);
    }
}
