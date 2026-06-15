package com.sasac.platform.asset.lifecycle.listener;

import com.sasac.platform.asset.lifecycle.event.AssetLifecycleApplicationEvent;
import com.sasac.platform.asset.lifecycle.service.AssetLifecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetLifecycleListener {

    private final AssetLifecycleService lifecycleService;

    @Async
    @EventListener
    public void onLifecycleEvent(AssetLifecycleApplicationEvent event) {
        try {
            lifecycleService.recordEvent(
                    event.getTenantId(),
                    event.getAssetId(),
                    event.getEventType(),
                    event.getEventTitle(),
                    event.getEventDetail(),
                    event.getSourceTable(),
                    event.getSourceId(),
                    event.getOperatorId(),
                    event.getOperatorName(),
                    event.getEventTime()
            );
        } catch (Exception e) {
            log.error("Failed to record lifecycle event for asset {}: {}",
                    event.getAssetId(), e.getMessage(), e);
        }
    }
}
