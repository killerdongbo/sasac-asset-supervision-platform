package com.sasac.platform.investment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.investment.dto.InvestDealDTO;
import com.sasac.platform.investment.dto.InvestExitDTO;
import com.sasac.platform.investment.dto.InvestPlanCreateDTO;
import com.sasac.platform.investment.dto.InvestPostDTO;
import com.sasac.platform.investment.dto.InvestProjectCreateDTO;
import com.sasac.platform.investment.dto.InvestProjectQueryDTO;
import com.sasac.platform.investment.entity.InvestDD;
import com.sasac.platform.investment.entity.InvestDeal;
import com.sasac.platform.investment.entity.InvestEquityNode;
import com.sasac.platform.investment.entity.InvestExit;
import com.sasac.platform.investment.entity.InvestPlan;
import com.sasac.platform.investment.entity.InvestPost;
import com.sasac.platform.investment.entity.InvestProject;
import com.sasac.platform.investment.mapper.InvestDDMapper;
import com.sasac.platform.investment.mapper.InvestDealMapper;
import com.sasac.platform.investment.mapper.InvestEquityNodeMapper;
import com.sasac.platform.investment.mapper.InvestExitMapper;
import com.sasac.platform.investment.mapper.InvestPlanMapper;
import com.sasac.platform.investment.mapper.InvestPostMapper;
import com.sasac.platform.investment.mapper.InvestProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for investment management CRUD and lifecycle operations.
 */
@Service
@RequiredArgsConstructor
public class InvestService {

    private final InvestPlanMapper investPlanMapper;
    private final InvestProjectMapper investProjectMapper;
    private final InvestDDMapper investDDMapper;
    private final InvestDealMapper investDealMapper;
    private final InvestPostMapper investPostMapper;
    private final InvestExitMapper investExitMapper;
    private final InvestEquityNodeMapper investEquityNodeMapper;

    /**
     * Creates an annual investment plan.
     *
     * @param dto the plan creation data
     * @return the created plan
     */
    @Transactional
    public InvestPlan createPlan(InvestPlanCreateDTO dto) {
        InvestPlan plan = new InvestPlan();
        BeanUtils.copyProperties(dto, plan);
        plan.setStatus("DRAFT");
        investPlanMapper.insert(plan);
        return plan;
    }

    /**
     * Creates an investment project with auto-generated project number.
     *
     * @param dto the project creation data
     * @return the created project
     */
    @Transactional
    public InvestProject createProject(InvestProjectCreateDTO dto) {
        InvestProject project = new InvestProject();
        BeanUtils.copyProperties(dto, project);

        // Generate project number: IV-{timestamp}
        String projectNo = "IV-" + System.currentTimeMillis();
        project.setProjectNo(projectNo);
        project.setPhase("PRE_INVEST");
        project.setStatus("ACTIVE");

        investProjectMapper.insert(project);
        return project;
    }

    /**
     * Queries investment projects with keyword search, phase/status/type filter, and pagination.
     *
     * @param q the query DTO
     * @return paginated project list
     */
    public Page<InvestProject> queryProjects(InvestProjectQueryDTO q) {
        LambdaQueryWrapper<InvestProject> wrapper = new LambdaQueryWrapper<>();

        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            wrapper.and(w -> w
                    .like(InvestProject::getProjectName, q.getKeyword())
                    .or()
                    .like(InvestProject::getProjectNo, q.getKeyword())
                    .or()
                    .like(InvestProject::getTargetCompany, q.getKeyword())
            );
        }
        if (q.getInvestType() != null && !q.getInvestType().isBlank()) {
            wrapper.eq(InvestProject::getInvestType, q.getInvestType());
        }
        if (q.getStatus() != null && !q.getStatus().isBlank()) {
            wrapper.eq(InvestProject::getStatus, q.getStatus());
        }
        if (q.getPhase() != null && !q.getPhase().isBlank()) {
            wrapper.eq(InvestProject::getPhase, q.getPhase());
        }

        wrapper.orderByDesc(InvestProject::getId);

        return investProjectMapper.selectPage(
                new Page<>(q.getPage(), q.getLimit()),
                wrapper
        );
    }

    /**
     * Retrieves an investment project by ID.
     *
     * @param id the project ID
     * @return the project
     * @throws BusinessException if the project is not found
     */
    public InvestProject getProjectById(Long id) {
        InvestProject project = investProjectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("投资项目不存在");
        }
        return project;
    }

    /**
     * Updates an investment project. Immutable fields are protected from modification.
     *
     * @param id     the project ID
     * @param update the object containing fields to update
     * @return the updated project
     */
    @Transactional
    public InvestProject updateProject(Long id, InvestProject update) {
        InvestProject existing = getProjectById(id);

        BeanUtils.copyProperties(update, existing, "id", "projectNo", "tenantId",
                "orgId", "createdAt", "updatedAt", "deleted");

        existing.setUpdatedAt(LocalDateTime.now());
        investProjectMapper.updateById(existing);

        return investProjectMapper.selectById(id);
    }

    /**
     * Soft-deletes an investment project by ID.
     *
     * @param id the project ID
     */
    @Transactional
    public void deleteProject(Long id) {
        getProjectById(id);
        investProjectMapper.deleteById(id);
    }

    /**
     * Records a due diligence report for a project.
     *
     * @param dd the due diligence data
     * @return the created record
     */
    @Transactional
    public InvestDD recordDD(InvestDD dd) {
        investDDMapper.insert(dd);
        return dd;
    }

    /**
     * Records a deal / transaction for a project.
     *
     * @param dto the deal data
     * @return the created deal record
     */
    @Transactional
    public InvestDeal recordDeal(InvestDealDTO dto) {
        InvestDeal deal = new InvestDeal();
        BeanUtils.copyProperties(dto, deal);
        deal.setStatus("PENDING");
        investDealMapper.insert(deal);
        return deal;
    }

    /**
     * Records a post-investment monitoring snapshot.
     *
     * @param dto the post-investment data
     * @return the created post record
     */
    @Transactional
    public InvestPost recordPost(InvestPostDTO dto) {
        InvestPost post = new InvestPost();
        BeanUtils.copyProperties(dto, post);
        investPostMapper.insert(post);
        return post;
    }

    /**
     * Records an investment exit.
     *
     * @param dto the exit data
     * @return the created exit record
     */
    @Transactional
    public InvestExit recordExit(InvestExitDTO dto) {
        InvestExit exit = new InvestExit();
        BeanUtils.copyProperties(dto, exit);
        exit.setStatus("DRAFT");
        investExitMapper.insert(exit);
        return exit;
    }

    /**
     * Builds the equity penetration tree for a project.
     * <p>
     * Queries all equity nodes for the tenant, then builds a hierarchical tree
     * by matching parentId references. Returns root-level nodes with nested children.
     *
     * @param projectId the project ID
     * @param tenantId  the tenant ID
     * @return root nodes with nested children, or a single root if it exists
     */
    public InvestEquityNode buildEquityTree(Long projectId, Long tenantId) {
        LambdaQueryWrapper<InvestEquityNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestEquityNode::getTenantId, tenantId);
        wrapper.eq(InvestEquityNode::getProjectId, projectId);
        wrapper.orderByAsc(InvestEquityNode::getLevel);

        List<InvestEquityNode> allNodes = investEquityNodeMapper.selectList(wrapper);

        if (allNodes.isEmpty()) {
            throw new BusinessException("未找到股权穿透数据");
        }

        // Build node map for O(n) tree construction
        Map<Long, InvestEquityNode> nodeMap = allNodes.stream()
                .collect(Collectors.toMap(InvestEquityNode::getId, n -> n));

        // Attach children to parents
        List<InvestEquityNode> roots = new ArrayList<>();
        for (InvestEquityNode node : allNodes) {
            if (node.getParentId() != null && nodeMap.containsKey(node.getParentId())) {
                InvestEquityNode parent = nodeMap.get(node.getParentId());
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        // Sort children by equity_pct descending for deterministic display
        for (InvestEquityNode node : allNodes) {
            if (node.getChildren() != null) {
                node.getChildren().sort(Comparator.comparing(InvestEquityNode::getEquityPct,
                        Comparator.nullsLast(Comparator.reverseOrder())));
            }
        }

        // If there's exactly one root, return it directly (clean tree display)
        if (roots.size() == 1) {
            return roots.get(0);
        }

        // Multiple roots: create a virtual root
        InvestEquityNode virtualRoot = new InvestEquityNode();
        virtualRoot.setCompanyName("投资主体");
        virtualRoot.setLevel(0);
        virtualRoot.setChildren(roots);
        return virtualRoot;
    }

    /**
     * Retrieves DD records for a project.
     */
    public List<InvestDD> getDDRecords(Long projectId, Long tenantId) {
        LambdaQueryWrapper<InvestDD> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestDD::getProjectId, projectId);
        wrapper.eq(InvestDD::getTenantId, tenantId);
        wrapper.orderByDesc(InvestDD::getId);
        return investDDMapper.selectList(wrapper);
    }

    /**
     * Retrieves deal records for a project.
     */
    public List<InvestDeal> getDealRecords(Long projectId, Long tenantId) {
        LambdaQueryWrapper<InvestDeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestDeal::getProjectId, projectId);
        wrapper.eq(InvestDeal::getTenantId, tenantId);
        wrapper.orderByDesc(InvestDeal::getId);
        return investDealMapper.selectList(wrapper);
    }

    /**
     * Retrieves post-investment records for a project.
     */
    public List<InvestPost> getPostRecords(Long projectId, Long tenantId) {
        LambdaQueryWrapper<InvestPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestPost::getProjectId, projectId);
        wrapper.eq(InvestPost::getTenantId, tenantId);
        wrapper.orderByDesc(InvestPost::getReportDate);
        return investPostMapper.selectList(wrapper);
    }

    /**
     * Retrieves exit records for a project.
     */
    public List<InvestExit> getExitRecords(Long projectId, Long tenantId) {
        LambdaQueryWrapper<InvestExit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InvestExit::getProjectId, projectId);
        wrapper.eq(InvestExit::getTenantId, tenantId);
        wrapper.orderByDesc(InvestExit::getId);
        return investExitMapper.selectList(wrapper);
    }
}
