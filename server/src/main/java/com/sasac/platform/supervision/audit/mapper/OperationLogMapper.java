package com.sasac.platform.supervision.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.supervision.audit.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for {@link OperationLog}.
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
