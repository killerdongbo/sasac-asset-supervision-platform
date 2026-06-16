package com.sasac.platform.supervision.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.supervision.approval.entity.ApprovalInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for {@link ApprovalInstance} entity.
 */
@Mapper
public interface ApprovalInstanceMapper extends BaseMapper<ApprovalInstance> {

    /**
     * Finds all pending instances where the current node's approver role matches.
     * Uses a JOIN for efficient single-query retrieval instead of in-memory filtering.
     *
     * @param tenantId the tenant ID
     * @param roleCode the approver role code
     * @return list of pending approval instances for the given role
     */
    @Select("<script>" +
            "SELECT DISTINCT i.* FROM approval_instance i " +
            "JOIN approval_node n ON i.def_id = n.def_id AND i.current_node = n.node_order " +
            "WHERE i.status = 'PENDING' " +
            "<if test='tenantId != null'>AND i.tenant_id = #{tenantId}</if> " +
            "<if test='roleCode != null'>AND n.approver_role = #{roleCode}</if> " +
            "AND n.deleted = 0 " +
            "AND i.deleted = 0 " +
            "ORDER BY i.created_at DESC" +
            "</script>")
    List<ApprovalInstance> selectPendingByRole(@Param("tenantId") Long tenantId,
                                               @Param("roleCode") String roleCode);
}
