package com.sasac.platform.hr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.hr.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for HrEmployee entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {
}
