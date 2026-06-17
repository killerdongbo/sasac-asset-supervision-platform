package com.sasac.platform.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.project.dto.AcceptanceDTO;
import com.sasac.platform.project.dto.ProjectCreateDTO;
import com.sasac.platform.project.dto.ProjectQueryDTO;
import com.sasac.platform.project.dto.ProgressRecordDTO;
import com.sasac.platform.project.entity.PmAcceptance;
import com.sasac.platform.project.entity.PmBudget;
import com.sasac.platform.project.entity.PmProject;
import com.sasac.platform.project.entity.PmProgress;
import com.sasac.platform.project.mapper.PmAcceptanceMapper;
import com.sasac.platform.project.mapper.PmBudgetMapper;
import com.sasac.platform.project.mapper.PmProjectMapper;
import com.sasac.platform.project.mapper.PmProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for project management CRUD and lifecycle operations.
 */
@Service
@RequiredArgsConstructor
public class PmProjectService {

    private final PmProjectMapper pmProjectMapper;
    private final PmProgressMapper pmProgressMapper;
    private final PmAcceptanceMapper pmAcceptanceMapper;
    private final PmBudgetMapper pmBudgetMapper;

    /**
     * Creates a new project with auto-generated project number and DRAFT status.
     *
     * @param dto the project creation data
     * @return the created project
     */
    @Transactional
    public PmProject create(ProjectCreateDTO dto) {
        PmProject project = new PmProject();
        BeanUtils.copyProperties(dto, project);

        // Generate project number: XM-{timestamp}
        String projectNo = "XM-" + System.currentTimeMillis();
        project.setProjectNo(projectNo);
        project.setStatus("DRAFT");

        pmProjectMapper.insert(project);

        return project;
    }

    /**
     * Queries projects with keyword search, status filter, and pagination.
     *
     * @param q the query DTO
     * @return paginated project list
     */
    public Page<PmProject> query(ProjectQueryDTO q) {
        LambdaQueryWrapper<PmProject> wrapper = new LambdaQueryWrapper<>();

        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(PmProject::getName, q.getKeyword())
                    .or()
                    .like(PmProject::getProjectNo, q.getKeyword())
            );
        }
        if (q.getStatus() != null && !q.getStatus().isBlank()) {
            wrapper.eq(PmProject::getStatus, q.getStatus());
        }
        if (q.getProjectType() != null && !q.getProjectType().isBlank()) {
            wrapper.eq(PmProject::getProjectType, q.getProjectType());
        }

        wrapper.orderByDesc(PmProject::getId);

        return pmProjectMapper.selectPage(
                new Page<>(q.getPage(), q.getLimit()),
                wrapper
        );
    }

    /**
     * Retrieves a project by ID.
     *
     * @param id the project ID
     * @return the project
     * @throws BusinessException if the project is not found
     */
    public PmProject getById(Long id) {
        PmProject project = pmProjectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        return project;
    }

    /**
     * Updates an existing project. Immutable fields are protected from modification.
     *
     * @param id     the project ID
     * @param update the object containing fields to update
     * @return the updated project
     */
    @Transactional
    public PmProject update(Long id, PmProject update) {
        PmProject existing = getById(id);

        BeanUtils.copyProperties(update, existing, "id", "projectNo", "tenantId",
                "orgId", "createdAt", "updatedAt", "deleted");

        pmProjectMapper.updateById(existing);

        return pmProjectMapper.selectById(id);
    }

    /**
     * Soft-deletes a project by ID.
     *
     * @param id the project ID
     */
    @Transactional
    public void delete(Long id) {
        getById(id);
        pmProjectMapper.deleteById(id);
    }

    /**
     * Records a progress report for a project.
     *
     * @param dto the progress record data
     * @return the created progress record
     */
    @Transactional
    public PmProgress recordProgress(ProgressRecordDTO dto) {
        PmProgress progress = new PmProgress();
        BeanUtils.copyProperties(dto, progress);

        pmProgressMapper.insert(progress);

        // Update project progress if provided
        if (dto.getProgressPct() != null) {
            PmProject project = getById(dto.getProjectId());
            // progress is stored per-record; the latest pct reflects current status
            project.setUpdatedAt(LocalDateTime.now());
            pmProjectMapper.updateById(project);
        }

        return progress;
    }

    /**
     * Retrieves progress history for a project, ordered by report date descending.
     *
     * @param projectId the project ID
     * @param tenantId  the tenant ID
     * @return list of progress records
     */
    public List<PmProgress> getProgressHistory(Long projectId, Long tenantId) {
        LambdaQueryWrapper<PmProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmProgress::getProjectId, projectId);
        wrapper.eq(PmProgress::getTenantId, tenantId);
        wrapper.orderByDesc(PmProgress::getReportDate);

        return pmProgressMapper.selectList(wrapper);
    }

    /**
     * Completes a project acceptance: updates status to COMPLETED and inserts
     * an acceptance record.
     *
     * @param id  the project ID
     * @param dto the acceptance data
     */
    @Transactional
    public void acceptProject(Long id, AcceptanceDTO dto) {
        PmProject project = getById(id);

        project.setStatus("COMPLETED");
        project.setActualEndDate(dto.getAcceptanceDate());
        project.setUpdatedAt(LocalDateTime.now());
        pmProjectMapper.updateById(project);

        PmAcceptance acceptance = new PmAcceptance();
        BeanUtils.copyProperties(dto, acceptance);
        acceptance.setProjectId(id);

        pmAcceptanceMapper.insert(acceptance);
    }

    /**
     * Retrieves acceptance records for a project.
     *
     * @param projectId the project ID
     * @return list of acceptance records
     */
    public List<PmAcceptance> getAcceptanceByProjectId(Long projectId) {
        LambdaQueryWrapper<PmAcceptance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmAcceptance::getProjectId, projectId);
        return pmAcceptanceMapper.selectList(wrapper);
    }

    /**
     * Retrieves budget records for a project.
     *
     * @param projectId the project ID
     * @return list of budget records
     */
    public List<PmBudget> getBudgetsByProjectId(Long projectId) {
        LambdaQueryWrapper<PmBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmBudget::getProjectId, projectId);
        return pmBudgetMapper.selectList(wrapper);
    }
}
