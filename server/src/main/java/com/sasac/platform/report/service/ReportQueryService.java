package com.sasac.platform.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.mapper.OrganizationMapper;
import com.sasac.platform.report.dto.OrgAssetSummary;
import com.sasac.platform.report.dto.ReportQueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportQueryService {

    private final AssetMapper assetMapper;
    private final OrganizationMapper organizationMapper;

    public List<Asset> queryAssets(ReportQueryParam param) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Asset::getTenantId, param.getTenantId());

        if (param.getOrgId() != null) {
            wrapper.eq(Asset::getOrgId, param.getOrgId());
        }
        if (param.getCategory() != null && !param.getCategory().isBlank()) {
            wrapper.eq(Asset::getCategory, param.getCategory());
        }
        if (param.getUseStatus() != null && !param.getUseStatus().isBlank()) {
            wrapper.eq(Asset::getUseStatus, param.getUseStatus());
        }
        if (param.getStartDate() != null && !param.getStartDate().isBlank()) {
            wrapper.ge(Asset::getCreatedAt, LocalDate.parse(param.getStartDate()).atStartOfDay());
        }
        if (param.getEndDate() != null && !param.getEndDate().isBlank()) {
            wrapper.le(Asset::getCreatedAt, LocalDate.parse(param.getEndDate()).atTime(23, 59, 59));
        }

        return assetMapper.selectList(wrapper);
    }

    public List<OrgAssetSummary> summarizeByOrg(ReportQueryParam param) {
        List<Asset> assets = queryAssets(param);
        List<Organization> orgs = organizationMapper.selectList(
                new LambdaQueryWrapper<Organization>()
                        .eq(Organization::getTenantId, param.getTenantId())
                        .eq(Organization::getStatus, 1)
        );
        Map<Long, String> orgNameMap = orgs.stream()
                .collect(Collectors.toMap(Organization::getId, Organization::getName, (a, b) -> a));

        Map<Long, List<Asset>> grouped = assets.stream()
                .collect(Collectors.groupingBy(Asset::getOrgId));

        return grouped.entrySet().stream().map(entry -> {
            List<Asset> orgAssets = entry.getValue();
            BigDecimal origVal = sumField(orgAssets, true);
            BigDecimal curVal = sumField(orgAssets, false);
            BigDecimal depr = orgAssets.stream()
                    .map(a -> a.getAccumulatedDepreciation() != null ? a.getAccumulatedDepreciation() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return OrgAssetSummary.builder()
                    .orgId(entry.getKey())
                    .orgName(orgNameMap.getOrDefault(entry.getKey(), "未知"))
                    .assetCount(orgAssets.size())
                    .originalValue(origVal)
                    .currentValue(curVal)
                    .accumulatedDepreciation(depr)
                    .build();
        }).toList();
    }

    public Map<String, List<Asset>> groupByCategory(ReportQueryParam param) {
        return queryAssets(param).stream()
                .collect(Collectors.groupingBy(Asset::getCategory));
    }

    public Map<String, List<Asset>> groupByStatus(ReportQueryParam param) {
        return queryAssets(param).stream()
                .collect(Collectors.groupingBy(Asset::getUseStatus));
    }

    private BigDecimal sumField(List<Asset> assets, boolean original) {
        return assets.stream()
                .map(a -> {
                    BigDecimal val = original ? a.getOriginalValue() : a.getCurrentValue();
                    return val != null ? val : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
