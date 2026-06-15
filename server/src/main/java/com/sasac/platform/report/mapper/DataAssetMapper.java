package com.sasac.platform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.report.entity.DataAsset;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for DataAsset entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface DataAssetMapper extends BaseMapper<DataAsset> {
}
