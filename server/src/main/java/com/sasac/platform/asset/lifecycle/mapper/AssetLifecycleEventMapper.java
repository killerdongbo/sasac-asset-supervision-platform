package com.sasac.platform.asset.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.asset.lifecycle.entity.AssetLifecycleEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssetLifecycleEventMapper extends BaseMapper<AssetLifecycleEvent> {
}
