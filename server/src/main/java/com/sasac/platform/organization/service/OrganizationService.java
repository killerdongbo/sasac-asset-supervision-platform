package com.sasac.platform.organization.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.mapper.OrganizationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing the organization tree structure.
 * <p>
 * Provides CRUD operations with integrity checks:
 * <ul>
 *   <li>Cycle detection on parent updates to prevent circular references</li>
 *   <li>Child existence check on delete to prevent orphaned nodes</li>
 *   <li>Recursive CTE queries for tree traversal</li>
 * </ul>
 */
@Service
public class OrganizationService {

    private final OrganizationMapper organizationMapper;

    public OrganizationService(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    /**
     * Creates a new organization node.
     *
     * @param org the organization to create
     * @return the created organization with generated ID
     */
    @Transactional
    public Organization create(Organization org) {
        organizationMapper.insert(org);
        return org;
    }

    /**
     * Updates an existing organization with cycle detection.
     * <p>
     * If the parentId is being changed, this method verifies that the
     * new parent is not a descendant of the current node (which would
     * create a circular reference). Also rejects setting self as parent.
     *
     * @param org the organization with updated fields
     * @return the updated organization
     * @throws BusinessException if the organization is not found or
     *                           a cycle would be created
     */
    @Transactional
    public Organization update(Organization org) {
        Organization existing = getById(org.getId());

        if (org.getParentId() != null) {
            // Cannot set self as parent
            if (org.getParentId().equals(org.getId())) {
                throw new BusinessException("不能将自己设为自己的父节点");
            }

            // Cycle detection: new parent cannot be a descendant of current org
            List<Organization> descendants = getDescendants(org.getId());
            boolean isDescendant = descendants.stream()
                    .anyMatch(d -> d.getId().equals(org.getParentId()));
            if (isDescendant) {
                throw new BusinessException("循环引用：不能将子节点设为自己的父节点");
            }
        }

        organizationMapper.updateById(org);
        return org;
    }

    /**
     * Retrieves an organization by ID.
     *
     * @param id the organization ID
     * @return the organization
     * @throws BusinessException if the organization is not found
     */
    public Organization getById(Long id) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) {
            throw new BusinessException("组织不存在: " + id);
        }
        return org;
    }

    /**
     * Gets direct children of a parent organization, ordered by sort_order.
     *
     * @param parentId the parent organization ID
     * @return list of direct child organizations
     */
    public List<Organization> getChildren(Long parentId) {
        return organizationMapper.findByParentId(parentId);
    }

    /**
     * Gets the full subtree (all descendants) of an organization.
     *
     * @param id the organization ID
     * @return list of descendant organizations
     */
    public List<Organization> getDescendants(Long id) {
        return organizationMapper.findDescendants(id);
    }

    /**
     * Deletes an organization if it has no children.
     * <p>
     * Uses MyBatis-Plus soft delete ({@code deleted = 1}).
     *
     * @param id the organization ID to delete
     * @throws BusinessException if the organization has existing children
     */
    @Transactional
    public void delete(Long id) {
        List<Organization> children = getChildren(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子节点，无法删除");
        }
        organizationMapper.deleteById(id);
    }
}
