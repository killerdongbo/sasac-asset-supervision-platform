package com.sasac.platform.supervision.audit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.lifecycle.entity.AssetLifecycleEvent;
import com.sasac.platform.asset.lifecycle.mapper.AssetLifecycleEventMapper;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.supervision.audit.entity.OperationLog;
import com.sasac.platform.supervision.audit.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final OperationLogMapper operationLogMapper;
    private final AssetLifecycleEventMapper lifecycleEventMapper;

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<OperationLog>>> listLogs(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(OperationLog::getTargetType, targetType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(OperationLog::getOperatorName, keyword)
                    .or().like(OperationLog::getAction, keyword));
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(operationLogMapper.selectList(wrapper)));
    }

    private static final Set<String> CHANGE_TYPES = Set.of(
            "RENTAL", "DISPOSAL", "STATUS_CHANGE", "DEPRECIATION", "TRANSFER", "VALUE_CHANGE");

    @GetMapping("/asset-lifecycle/{assetId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLifecycle(
            @PathVariable Long assetId) {
        LambdaQueryWrapper<AssetLifecycleEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLifecycleEvent::getAssetId, assetId);
        wrapper.orderByDesc(AssetLifecycleEvent::getEventTime);
        List<AssetLifecycleEvent> events = lifecycleEventMapper.selectList(wrapper);

        List<Map<String, Object>> result = events.stream().map(e -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("time", e.getEventTime() != null ? e.getEventTime().toString() : "");
            item.put("type", CHANGE_TYPES.contains(e.getEventType()) ? "CHANGE" : "OPERATION");
            item.put("field", e.getEventTitle() != null ? e.getEventTitle() : (e.getEventType() + " — " + e.getSourceTable()));
            item.put("details", e.getOperatorName() != null
                    ? "操作人: " + e.getOperatorName() + (e.getEventDetail() != null ? " | " + e.getEventDetail() : "")
                    : (e.getEventDetail() != null ? e.getEventDetail() : ""));
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
