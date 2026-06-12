package com.sasac.platform.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for SASAC supervision dashboard data aggregation.
 * <p>
 * Provides tenant-wide overview statistics, category/status distributions,
 * and top organizations ranked by asset value.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AssetMapper assetMapper;

    /**
     * Retrieves tenant-wide dashboard overview statistics.
     * <p>
     * Aggregates all assets for the given tenant to produce:
     * <ul>
     *   <li>totalAssets - total count of assets</li>
     *   <li>totalOriginalValue - sum of original values</li>
     *   <li>totalCurrentValue - sum of current (net) values</li>
     *   <li>totalAccumulatedDepreciation - sum of accumulated depreciation</li>
     *   <li>avgDepreciationRate - (totalDepreciation / totalOriginalValue) * 100, rounded to 2 decimals</li>
     *   <li>categoryDistribution - map of category to sum of originalValue</li>
     *   <li>statusDistribution - map of useStatus to asset count</li>
     * </ul>
     *
     * @param tenantId the tenant ID for multi-tenant filtering
     * @return a Map containing all overview fields
     */
    public Map<String, Object> getOverview(Long tenantId) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getTenantId, tenantId);

        List<Asset> assets = assetMapper.selectList(wrapper);

        Map<String, Object> result = new LinkedHashMap<>();

        // Total asset count
        result.put("totalAssets", assets.size());

        // Financial aggregates with null-safe handling
        BigDecimal totalOriginalValue = assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalOriginalValue", totalOriginalValue);

        BigDecimal totalCurrentValue = assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalCurrentValue", totalCurrentValue);

        BigDecimal totalAccumulatedDepreciation = assets.stream()
                .map(a -> a.getAccumulatedDepreciation() != null ? a.getAccumulatedDepreciation() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalAccumulatedDepreciation", totalAccumulatedDepreciation);

        // Average depreciation rate: (totalDepreciation / totalOriginalValue) * 100
        BigDecimal avgDepreciationRate = BigDecimal.ZERO;
        if (totalOriginalValue.compareTo(BigDecimal.ZERO) > 0) {
            avgDepreciationRate = totalAccumulatedDepreciation
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalOriginalValue, 2, RoundingMode.HALF_UP);
        }
        result.put("avgDepreciationRate", avgDepreciationRate);

        // Category distribution: group by category, sum originalValue
        Map<String, BigDecimal> categoryDistribution = assets.stream()
                .collect(Collectors.groupingBy(
                        Asset::getCategory,
                        Collectors.mapping(
                                a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
        result.put("categoryDistribution", categoryDistribution);

        // Status distribution: group by useStatus, count
        Map<String, Long> statusDistribution = assets.stream()
                .collect(Collectors.groupingBy(Asset::getUseStatus, Collectors.counting()));
        result.put("statusDistribution", statusDistribution);

        return result;
    }

    /**
     * Retrieves the top N organizations by total asset value for a tenant.
     * <p>
     * Groups assets by orgId, sums their originalValue, sorts descending
     * by total value, and limits to the specified number of entries.
     *
     * @param tenantId the tenant ID for multi-tenant filtering
     * @param limit    the maximum number of organizations to return
     * @return a list of maps, each containing orgId and totalValue
     */
    public List<Map<String, Object>> getTopOrgsByValue(Long tenantId, int limit) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getTenantId, tenantId);

        List<Asset> assets = assetMapper.selectList(wrapper);

        // Group by orgId, sum originalValue
        Map<Long, BigDecimal> orgValues = assets.stream()
                .collect(Collectors.groupingBy(
                        Asset::getOrgId,
                        Collectors.mapping(
                                a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // Sort descending by total value, apply limit, map to result format
        return orgValues.entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("orgId", entry.getKey());
                    item.put("totalValue", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
