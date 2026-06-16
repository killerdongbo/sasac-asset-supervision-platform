package com.sasac.platform.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.workflow.entity.WorkflowInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link WorkflowInstance} entity.
 */
@Mapper
public interface WorkflowInstanceMapper extends BaseMapper<WorkflowInstance> {
}
