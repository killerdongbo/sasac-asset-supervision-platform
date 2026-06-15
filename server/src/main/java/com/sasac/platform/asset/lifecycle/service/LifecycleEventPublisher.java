package com.sasac.platform.asset.lifecycle.service;

import com.sasac.platform.asset.lifecycle.event.AssetLifecycleApplicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LifecycleEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(Long tenantId, Long assetId, String eventType,
                        String eventTitle, String eventDetail,
                        String sourceTable, Long sourceId,
                        Long operatorId, String operatorName) {
        eventPublisher.publishEvent(new AssetLifecycleApplicationEvent(
                this, tenantId, assetId, eventType, eventTitle,
                eventDetail, sourceTable, sourceId,
                operatorId, operatorName, LocalDateTime.now()
        ));
    }
}
