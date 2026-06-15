package com.sasac.platform.screen.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.auth.mapper.UserMapper;
import com.sasac.platform.screen.dto.ScreenDataDTO;
import com.sasac.platform.screen.dto.ScreenDataDTO.*;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenDataService {

    private final AssetMapper assetMapper;
    private final OrganizationMapper orgMapper;
    private final UserMapper userMapper;

    public ScreenDataDTO getScreenData(Long tenantId) {
        List<Asset> allAssets = assetMapper.selectList(
                new LambdaQueryWrapper<Asset>().eq(Asset::getTenantId, tenantId)
        );

        return ScreenDataDTO.builder()
                .overview(buildOverview(allAssets, tenantId))
                .regionDistribution(buildRegionDistribution(allAssets))
                .statusDistribution(buildStatusDistribution(allAssets))
                .monthlyTrend(buildMonthlyTrend(allAssets))
                .topOrgs(buildTopOrgs(allAssets, tenantId))
                .recentAlerts(buildRecentAlerts())
                .build();
    }

    private OverviewData buildOverview(List<Asset> assets, Long tenantId) {
        BigDecimal totalOriginal = assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrent = assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        long monthNew = assets.stream()
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().toLocalDate().isAfter(monthStart.minusDays(1)))
                .count();

        long orgCount = orgMapper.selectCount(
                new LambdaQueryWrapper<Organization>().eq(Organization::getTenantId, tenantId)
        );
        long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<com.sasac.platform.auth.entity.User>()
                        .eq(com.sasac.platform.auth.entity.User::getTenantId, tenantId)
        );

        return OverviewData.builder()
                .totalAssets(assets.size())
                .totalOriginalValue(totalOriginal)
                .totalCurrentValue(totalCurrent)
                .monthNewAssets(monthNew)
                .totalOrgs(orgCount)
                .totalUsers(userCount)
                .alertCount(5)
                .build();
    }

    private List<RegionAsset> buildRegionDistribution(List<Asset> assets) {
        Map<String, List<Asset>> grouped = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getLocation() != null ? a.getLocation() : "未知"));
        return grouped.entrySet().stream()
                .map(e -> RegionAsset.builder()
                        .name(e.getKey())
                        .value(e.getValue().size())
                        .amount(e.getValue().stream()
                                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .toList();
    }

    private List<StatusPie> buildStatusDistribution(List<Asset> assets) {
        Map<String, Long> grouped = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getUseStatus() != null ? a.getUseStatus() : "UNKNOWN", Collectors.counting()));
        Map<String, String> labels = Map.of("IN_USE", "在用", "IDLE", "闲置", "DISPOSED", "已处置", "RENTED", "出租", "UNKNOWN", "未知");
        return grouped.entrySet().stream()
                .map(e -> StatusPie.builder().name(labels.getOrDefault(e.getKey(), e.getKey())).value(e.getValue()).build())
                .toList();
    }

    private List<MonthTrend> buildMonthlyTrend(List<Asset> assets) {
        List<MonthTrend> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            String month = ym.format(fmt);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            List<Asset> monthAssets = assets.stream()
                    .filter(a -> a.getCreatedAt() != null
                            && !a.getCreatedAt().toLocalDate().isBefore(start)
                            && !a.getCreatedAt().toLocalDate().isAfter(end))
                    .toList();
            BigDecimal val = monthAssets.stream()
                    .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(MonthTrend.builder().month(month).count(monthAssets.size()).value(val).build());
        }
        return result;
    }

    private List<OrgRank> buildTopOrgs(List<Asset> assets, Long tenantId) {
        List<Organization> orgs = orgMapper.selectList(
                new LambdaQueryWrapper<Organization>().eq(Organization::getTenantId, tenantId)
        );
        Map<Long, String> orgNameMap = orgs.stream().collect(Collectors.toMap(Organization::getId, Organization::getName));

        Map<Long, List<Asset>> grouped = assets.stream()
                .filter(a -> a.getOrgId() != null)
                .collect(Collectors.groupingBy(Asset::getOrgId));

        return grouped.entrySet().stream()
                .map(e -> OrgRank.builder()
                        .orgName(orgNameMap.getOrDefault(e.getKey(), "未知单位"))
                        .assetCount(e.getValue().size())
                        .totalValue(e.getValue().stream()
                                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .sorted((a, b) -> Long.compare(b.getAssetCount(), a.getAssetCount()))
                .limit(10)
                .toList();
    }

    private List<AlertItem> buildRecentAlerts() {
        return List.of(
                AlertItem.builder().title("办公设备A栋3台资产即将过保").level("WARNING").time("10分钟前").build(),
                AlertItem.builder().title("车辆管理处2辆车年检即将到期").level("INFO").time("30分钟前").build(),
                AlertItem.builder().title("IT设备折旧完成率超过90%").level("INFO").time("1小时前").build()
        );
    }
}
