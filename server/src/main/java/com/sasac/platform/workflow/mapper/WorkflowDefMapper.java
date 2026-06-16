package com.sasac.platform.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.workflow.entity.WorkflowDef;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link WorkflowDef} entity.
 */
@Mapper
public interface WorkflowDefMapper extends BaseMapper<WorkflowDef> {
}
