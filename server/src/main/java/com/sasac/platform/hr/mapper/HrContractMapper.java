package com.sasac.platform.hr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.hr.entity.HrContract;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for HrContract entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface HrContractMapper extends BaseMapper<HrContract> {
}
