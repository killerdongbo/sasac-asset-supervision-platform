package com.sasac.platform.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.workflow.entity.WorkflowTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link WorkflowTask} entity.
 */
@Mapper
public interface WorkflowTaskMapper extends BaseMapper<WorkflowTask> {
}
