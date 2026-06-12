package com.sasac.platform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.report.entity.Report;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for Report entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
}
