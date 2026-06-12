package com.sasac.platform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.asset.entity.Asset;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for Asset entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
}
