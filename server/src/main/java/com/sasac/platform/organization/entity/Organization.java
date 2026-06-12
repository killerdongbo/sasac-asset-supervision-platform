package com.sasac.platform.organization.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Organization entity representing nodes in the organization tree.
 * <p>
 * Supports hierarchical organization structure with types:
 * SASAC (国资委), GROUP (集团), ENTERPRISE (企业), DEPARTMENT (部门).
 */
@Getter
@Setter
@TableName("sys_organization")
public class Organization extends BaseEntity {

    /**
     * Parent organization ID. Null for root nodes.
     */
    private Long parentId;

    /**
     * Organization name.
     */
    private String name;

    /**
     * Organization type: SASAC, GROUP, ENTERPRISE, DEPARTMENT.
     */
    private String orgType;

    /**
     * Organization code (business code).
     */
    private String orgCode;

    /**
     * Tenant ID this organization belongs to.
     */
    private Long tenantId;

    /**
     * Sort order for display ordering. Defaults to 0.
     */
    private Integer sortOrder = 0;

    /**
     * Status: 1 = active, 0 = disabled.
     */
    private Integer status = 1;
}
