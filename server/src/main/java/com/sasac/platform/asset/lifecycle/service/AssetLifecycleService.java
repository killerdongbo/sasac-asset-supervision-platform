package com.sasac.platform.asset.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.lifecycle.dto.LifecycleSummaryDTO;
import com.sasac.platform.asset.lifecycle.entity.AssetLifecycleEvent;
import com.sasac.platform.asset.lifecycle.mapper.AssetLifecycleEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetLifecycleService {

    private final AssetLifecycleEventMapper lifecycleMapper;

    public void recordEvent(Long tenantId, Long assetId, String eventType,
                            String eventTitle, String eventDetail,
                            String sourceTable, Long sourceId,
                            Long operatorId, String operatorName,
                            LocalDateTime eventTime) {
        AssetLifecycleEvent event = new AssetLifecycleEvent();
        event.setTenantId(tenantId);
        event.setAssetId(assetId);
        event.setEventType(eventType);
        event.setEventTitle(eventTitle);
        event.setEventDetail(eventDetail);
        event.setSourceTable(sourceTable);
        event.setSourceId(sourceId);
        event.setOperatorId(operatorId);
        event.setOperatorName(operatorName);
        event.setEventTime(eventTime);
        lifecycleMapper.insert(event);
    }

    public Page<AssetLifecycleEvent> getTimeline(Long tenantId, Long assetId,
                                                  String eventType,
                                                  int page, int size,
                                                  boolean ascending) {
        Page<AssetLifecycleEvent> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AssetLifecycleEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLifecycleEvent::getTenantId, tenantId)
               .eq(AssetLifecycleEvent::getAssetId, assetId);

        if (eventType != null && !eventType.isBlank()) {
            wrapper.eq(AssetLifecycleEvent::getEventType, eventType);
        }

        if (ascending) {
            wrapper.orderByAsc(AssetLifecycleEvent::getEventTime);
        } else {
            wrapper.orderByDesc(AssetLifecycleEvent::getEventTime);
        }

        return lifecycleMapper.selectPage(pageParam, wrapper);
    }

    public LifecycleSummaryDTO getSummary(Long tenantId, Long assetId) {
        LambdaQueryWrapper<AssetLifecycleEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLifecycleEvent::getTenantId, tenantId)
               .eq(AssetLifecycleEvent::getAssetId, assetId)
               .orderByAsc(AssetLifecycleEvent::getEventTime);

        List<AssetLifecycleEvent> events = lifecycleMapper.selectList(wrapper);
        if (events.isEmpty()) {
            return LifecycleSummaryDTO.builder()
                    .totalEvents(0)
                    .build();
        }

        LocalDateTime first = events.get(0).getEventTime();
        LocalDateTime last = events.get(events.size() - 1).getEventTime();
        long days = ChronoUnit.DAYS.between(first, LocalDateTime.now());

        long transferCount = events.stream()
                .filter(e -> "TRANSFER".equals(e.getEventType()) || "ASSIGNMENT".equals(e.getEventType()))
                .count();
        long maintenanceCount = events.stream()
                .filter(e -> "MAINTENANCE".equals(e.getEventType()))
                .count();
        long inspectionCount = events.stream()
                .filter(e -> "INSPECTION".equals(e.getEventType()))
                .count();

        return LifecycleSummaryDTO.builder()
                .totalEvents(events.size())
                .firstEventTime(first.toString())
                .lastEventTime(last.toString())
                .daysInService(days)
                .transferCount(transferCount)
                .maintenanceCount(maintenanceCount)
                .inspectionCount(inspectionCount)
                .build();
    }
}
