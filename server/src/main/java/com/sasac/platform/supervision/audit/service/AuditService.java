package com.sasac.platform.supervision.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.supervision.audit.entity.OperationLog;
import com.sasac.platform.supervision.audit.mapper.OperationLogMapper;
import com.sasac.platform.asset.ledger.entity.AssetChangeLog;
import com.sasac.platform.asset.ledger.mapper.AssetChangeLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for querying audit trail data.
 * <p>
 * Provides methods to retrieve operation logs and to build a combined
 * asset lifecycle timeline by merging {@link OperationLog} entries with
 * {@link AssetChangeLog} entries.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final OperationLogMapper operationLogMapper;
    private final AssetChangeLogMapper assetChangeLogMapper;

    /**
     * Retrieves operation logs for a specific target type and ID.
     *
     * @param targetType the target entity type
     * @param targetId   the target entity ID
     * @return list of operation logs, ordered by creation time descending
     */
    public List<OperationLog> getLogs(String targetType, Long targetId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getTargetType, targetType)
                .eq(OperationLog::getTargetId, targetId)
                .orderByDesc(OperationLog::getCreatedAt);
        return operationLogMapper.selectList(wrapper);
    }

    /**
     * Builds a combined asset lifecycle timeline for the given asset.
     * <p>
     * Merges {@link OperationLog} entries (from the audit trail) with
     * {@link AssetChangeLog} entries (from field-level change tracking),
     * sorts them all by creation time, and returns a unified timeline.
     *
     * @param assetId the asset ID
     * @return list of timeline entries as Maps with "type", "timestamp", and "data" keys
     */
    public List<Map<String, Object>> getAssetLifecycle(Long assetId) {
        List<Map<String, Object>> timeline = new ArrayList<>();

        // Collect OperationLog entries
        List<OperationLog> logs = operationLogMapper.selectList(
                new LambdaQueryWrapper<OperationLog>()
                        .eq(OperationLog::getTargetType, "Asset")
                        .eq(OperationLog::getTargetId, assetId)
        );
        for (OperationLog log : logs) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("type", "OPERATION");
            entry.put("timestamp", log.getCreatedAt() != null ? log.getCreatedAt().toString() : "");

            Map<String, Object> data = new HashMap<>();
            data.put("id", log.getId());
            data.put("action", log.getAction());
            data.put("operatorName", log.getOperatorName());
            data.put("beforeData", log.getBeforeData());
            data.put("afterData", log.getAfterData());
            entry.put("data", data);

            timeline.add(entry);
        }

        // Collect AssetChangeLog entries
        List<AssetChangeLog> changes = assetChangeLogMapper.selectList(
                new LambdaQueryWrapper<AssetChangeLog>()
                        .eq(AssetChangeLog::getAssetId, assetId)
        );
        for (AssetChangeLog change : changes) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("type", "FIELD_CHANGE");
            entry.put("timestamp", change.getCreatedAt() != null ? change.getCreatedAt().toString() : "");

            Map<String, Object> data = new HashMap<>();
            data.put("id", change.getId());
            data.put("field", change.getChangeField());
            data.put("beforeValue", change.getBeforeValue());
            data.put("afterValue", change.getAfterValue());
            data.put("operatorId", change.getOperatorId());
            data.put("remark", change.getRemark() != null ? change.getRemark() : "");
            entry.put("data", data);

            timeline.add(entry);
        }

        // Sort by timestamp ascending
        timeline.sort(Comparator.comparing(e -> (String) e.get("timestamp")));

        return timeline;
    }
}
