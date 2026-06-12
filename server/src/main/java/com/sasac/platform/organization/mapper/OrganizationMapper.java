package com.sasac.platform.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.organization.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis-Plus mapper for {@link Organization} entity.
 * <p>
 * Provides recursive CTE queries for tree traversal:
 * <ul>
 *   <li>{@link #findByParentId} - direct children ordered by sort_order</li>
 *   <li>{@link #findDescendants} - full subtree using recursive CTE</li>
 *   <li>{@link #findAncestors} - path to root using recursive CTE</li>
 * </ul>
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    /**
     * Finds direct children of a given parent, ordered by sort_order.
     *
     * @param parentId the parent organization ID
     * @return list of direct child organizations
     */
    @Select("SELECT * FROM sys_organization WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<Organization> findByParentId(Long parentId);

    /**
     * Finds all descendants of a given organization using a recursive CTE.
     * <p>
     * The query walks down the tree from the given node, collecting all
     * descendants. The node itself is excluded from the result.
     *
     * @param id the organization ID to find descendants for
     * @return list of descendant organizations
     */
    @Select("""
            WITH RECURSIVE org_tree AS (
                SELECT * FROM sys_organization WHERE id = #{id} AND deleted = 0
                UNION ALL
                SELECT o.* FROM sys_organization o
                INNER JOIN org_tree t ON o.parent_id = t.id
                WHERE o.deleted = 0
            ) SELECT * FROM org_tree WHERE id != #{id}
            """)
    List<Organization> findDescendants(Long id);

    /**
     * Finds all ancestors of a given organization using a recursive CTE.
     * <p>
     * The query walks up the tree from the given node to the root.
     * The node itself is excluded from the result.
     *
     * @param id the organization ID to find ancestors for
     * @return list of ancestor organizations from parent to root
     */
    @Select("""
            WITH RECURSIVE org_tree AS (
                SELECT * FROM sys_organization WHERE id = #{id} AND deleted = 0
                UNION ALL
                SELECT o.* FROM sys_organization o
                INNER JOIN org_tree t ON o.id = t.parent_id
                WHERE o.deleted = 0
            ) SELECT * FROM org_tree WHERE id != #{id}
            """)
    List<Organization> findAncestors(Long id);
}
