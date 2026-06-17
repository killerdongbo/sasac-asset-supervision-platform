package com.sasac.platform.hr.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.hr.dto.PerformanceScoreDTO;
import com.sasac.platform.hr.entity.HrEmployee;
import com.sasac.platform.hr.entity.HrPerformance;
import com.sasac.platform.hr.mapper.HrEmployeeMapper;
import com.sasac.platform.hr.mapper.HrPerformanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for performance review management.
 * <p>
 * Handles creating performance reviews, scoring with automatic grade
 * calculation, and status transitions.
 */
@Service
@RequiredArgsConstructor
public class HrPerformanceService {

    private final HrPerformanceMapper hrPerformanceMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    /**
     * Creates a new performance review record.
     *
     * @param perf the performance review entity
     * @return the created performance record
     * @throws BusinessException if the employee is not found
     */
    @Transactional
    public HrPerformance create(HrPerformance perf) {
        HrEmployee employee = hrEmployeeMapper.selectById(perf.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        perf.setStatus("DRAFT");
        hrPerformanceMapper.insert(perf);
        return perf;
    }

    /**
     * Scores a performance review. Calculates final score as the average
     * of self and manager scores, then assigns a grade:
     * A (>= 90), B (>= 80), C (>= 70), D (< 70).
     *
     * @param id   the performance record ID
     * @param dto  the score data
     * @return the updated performance record
     * @throws BusinessException if the record is not found or already confirmed
     */
    @Transactional
    public HrPerformance score(Long id, PerformanceScoreDTO dto) {
        HrPerformance perf = hrPerformanceMapper.selectById(id);
        if (perf == null) {
            throw new BusinessException("绩效记录不存在");
        }
        if ("CONFIRMED".equals(perf.getStatus())) {
            throw new BusinessException("绩效记录已确认，无法修改评分");
        }

        perf.setSelfScore(dto.getSelfScore());
        perf.setManagerScore(dto.getManagerScore());

        // Final score = average of self and manager scores
        BigDecimal finalScore = dto.getSelfScore()
                .add(dto.getManagerScore())
                .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);
        perf.setFinalScore(finalScore);

        // Grade: A >= 90, B >= 80, C >= 70, D < 70
        String grade;
        if (finalScore.compareTo(new BigDecimal("90")) >= 0) {
            grade = "A";
        } else if (finalScore.compareTo(new BigDecimal("80")) >= 0) {
            grade = "B";
        } else if (finalScore.compareTo(new BigDecimal("70")) >= 0) {
            grade = "C";
        } else {
            grade = "D";
        }
        perf.setGrade(grade);

        hrPerformanceMapper.updateById(perf);
        return hrPerformanceMapper.selectById(id);
    }

    /**
     * Confirms a performance record, locking its score and grade.
     *
     * @param id the performance record ID
     * @return the confirmed performance record
     * @throws BusinessException if the record is not found
     */
    @Transactional
    public HrPerformance confirm(Long id) {
        HrPerformance perf = hrPerformanceMapper.selectById(id);
        if (perf == null) {
            throw new BusinessException("绩效记录不存在");
        }
        perf.setStatus("CONFIRMED");
        hrPerformanceMapper.updateById(perf);
        return hrPerformanceMapper.selectById(id);
    }

    /**
     * Queries performance records with filters and pagination.
     *
     * @param page    page number
     * @param limit   page size
     * @param tenantId  tenant ID filter (optional)
     * @param employeeId employee ID filter (optional)
     * @return paginated performance records
     */
    public Page<HrPerformance> query(int page, int limit, Long tenantId, Long employeeId) {
        LambdaQueryWrapper<HrPerformance> wrapper = new LambdaQueryWrapper<>();

        if (tenantId != null) {
            wrapper.eq(HrPerformance::getTenantId, tenantId);
        }
        if (employeeId != null) {
            wrapper.eq(HrPerformance::getEmployeeId, employeeId);
        }

        wrapper.orderByDesc(HrPerformance::getId);

        return hrPerformanceMapper.selectPage(
                new Page<>(page, limit),
                wrapper
        );
    }
}
