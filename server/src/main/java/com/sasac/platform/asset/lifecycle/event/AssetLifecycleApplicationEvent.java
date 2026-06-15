package com.sasac.platform.asset.lifecycle.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class AssetLifecycleApplicationEvent extends ApplicationEvent {

    private final Long tenantId;
    private final Long assetId;
    private final String eventType;
    private final String eventTitle;
    private final String eventDetail;
    private final String sourceTable;
    private final Long sourceId;
    private final Long operatorId;
    private final String operatorName;
    private final LocalDateTime eventTime;

    public AssetLifecycleApplicationEvent(
            Object source,
            Long tenantId,
            Long assetId,
            String eventType,
            String eventTitle,
            String eventDetail,
            String sourceTable,
            Long sourceId,
            Long operatorId,
            String operatorName,
            LocalDateTime eventTime) {
        super(source);
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.eventType = eventType;
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.sourceTable = sourceTable;
        this.sourceId = sourceId;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.eventTime = eventTime;
    }
}
