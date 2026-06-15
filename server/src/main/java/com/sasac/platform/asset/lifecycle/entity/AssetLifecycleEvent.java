package com.sasac.platform.asset.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("asset_lifecycle_event")
public class AssetLifecycleEvent extends BaseEntity {

    private Long tenantId;

    private Long assetId;

    private String eventType;

    private String eventTitle;

    private String eventDetail;

    private String sourceTable;

    private Long sourceId;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime eventTime;
}
