package com.sasac.platform.supervision.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.supervision.approval.entity.ApprovalNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for {@link ApprovalNode} entity.
 */
@Mapper
public interface ApprovalNodeMapper extends BaseMapper<ApprovalNode> {

    /**
     * Finds all nodes for a given definition, ordered by node_order ascending.
     *
     * @param defId the approval definition ID
     * @return ordered list of approval nodes
     */
    @Select("SELECT * FROM approval_node WHERE def_id = #{defId} AND deleted = 0 ORDER BY node_order ASC")
    List<ApprovalNode> selectByDefId(Long defId);
}
