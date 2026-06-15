package com.sasac.platform.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.entity.Depreciation;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.asset.mapper.DepreciationMapper;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.asset.inspection.entity.InspectionTask;
import com.sasac.platform.asset.inspection.mapper.InspectionTaskMapper;
import com.sasac.platform.asset.inventory.entity.InventoryTask;
import com.sasac.platform.asset.inventory.mapper.InventoryTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for statistics dashboards.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final AssetMapper assetMapper;
    private final DepreciationMapper depreciationMapper;
    private final InspectionTaskMapper inspectionTaskMapper;
    private final InventoryTaskMapper inventoryTaskMapper;

    /**
     * Returns asset statistics: total count, original value, current value,
     * and distribution by category and status.
     */
    @GetMapping("/assets")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssetStats() {
        List<Asset> assets = assetMapper.selectList(null);

        long totalCount = assets.size();
        BigDecimal totalOriginalValue = assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrentValue = assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Category distribution as Object for frontend Object.entries()
        Map<String, Double> byCategory = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getCategory() != null ? a.getCategory() : "未知",
                        Collectors.summingDouble(a -> a.getOriginalValue() != null
                                ? a.getOriginalValue().doubleValue() : 0)));

        // Status distribution as Object for frontend Object.entries()
        Map<String, Long> byStatus = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getUseStatus() != null ? a.getUseStatus() : "未知",
                        Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCount", (int) totalCount);
        result.put("totalOriginalValue", totalOriginalValue);
        result.put("totalCurrentValue", totalCurrentValue);
        result.put("byCategory", byCategory);
        result.put("byStatus", byStatus);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Returns depreciation statistics: total depreciation and monthly trend.
     */
    @GetMapping("/depreciation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDepreciationStats() {
        List<Depreciation> records = depreciationMapper.selectList(null);

        BigDecimal totalDepreciation = records.stream()
                .map(d -> d.getDepreciationAmount() != null ? d.getDepreciationAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgMonthly = records.isEmpty() ? BigDecimal.ZERO
                : totalDepreciation.divide(BigDecimal.valueOf(records.stream()
                        .map(Depreciation::getPeriod).filter(Objects::nonNull).distinct().count()),
                        RoundingMode.HALF_UP);

        // Monthly trend - return separate arrays for frontend chart
        Map<String, BigDecimal> monthlyData = records.stream()
                .filter(r -> r.getPeriod() != null)
                .collect(Collectors.groupingBy(Depreciation::getPeriod,
                        Collectors.mapping(r -> r.getDepreciationAmount() != null
                                ? r.getDepreciationAmount() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        List<String> monthLabels = new ArrayList<>(monthlyData.keySet());
        List<BigDecimal> amounts = new ArrayList<>(monthlyData.values());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDepreciation", totalDepreciation);
        result.put("avgMonthly", avgMonthly);
        result.put("months", monthLabels);
        result.put("amounts", amounts);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Returns inspection statistics by status.
     */
    @GetMapping("/inspection")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInspectionStats() {
        List<InspectionTask> tasks = inspectionTaskMapper.selectList(null);
        long total = tasks.size();
        long completed = tasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long inProgress = tasks.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long pending = tasks.stream().filter(t -> "PENDING".equals(t.getStatus())).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("inProgress", inProgress);
        result.put("pending", pending);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Returns inventory statistics by status.
     */
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryStats() {
        List<InventoryTask> tasks = inventoryTaskMapper.selectList(null);
        long total = tasks.size();
        long completed = tasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long inProgress = tasks.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long pending = tasks.stream().filter(t -> "PENDING".equals(t.getStatus())).count();
        long totalDiffs = tasks.stream()
                .mapToLong(t -> t.getDiffCount() != null ? t.getDiffCount() : 0).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("inProgress", inProgress);
        result.put("pending", pending);
        result.put("totalDiffs", totalDiffs);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
