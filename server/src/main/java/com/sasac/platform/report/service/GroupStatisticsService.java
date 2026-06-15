package com.sasac.platform.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.mapper.OrganizationMapper;
import com.sasac.platform.report.dto.GroupStatisticsDTO;
import com.sasac.platform.report.dto.GroupStatisticsDTO.CategoryStat;
import com.sasac.platform.report.dto.GroupStatisticsDTO.StatusStat;
import com.sasac.platform.report.dto.GroupStatisticsDTO.SubsidiaryStatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupStatisticsService {

    private final OrganizationMapper organizationMapper;
    private final AssetMapper assetMapper;

    public GroupStatisticsDTO getGroupStatistics(Long tenantId, Long orgId) {
        Organization org = organizationMapper.selectById(orgId);
        if (org == null) {
            throw new BusinessException("组织不存在");
        }

        List<Long> allOrgIds = collectSubOrgIds(tenantId, orgId);
        allOrgIds.add(orgId);

        List<Asset> assets = assetMapper.selectList(
                new LambdaQueryWrapper<Asset>()
                        .eq(Asset::getTenantId, tenantId)
                        .in(Asset::getOrgId, allOrgIds)
        );

        BigDecimal totalOriginal = assets.stream()
                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrent = assets.stream()
                .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryStat> categoryStats = assets.stream()
                .collect(Collectors.groupingBy(Asset::getCategory))
                .entrySet().stream()
                .map(e -> CategoryStat.builder()
                        .name(e.getKey())
                        .count(e.getValue().size())
                        .value(e.getValue().stream()
                                .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .toList();

        List<StatusStat> statusStats = assets.stream()
                .collect(Collectors.groupingBy(Asset::getUseStatus))
                .entrySet().stream()
                .map(e -> StatusStat.builder().name(e.getKey()).count(e.getValue().size()).build())
                .toList();

        List<SubsidiaryStatDTO> subsidiaries = buildSubsidiaryStats(tenantId, orgId, assets);

        return GroupStatisticsDTO.builder()
                .orgId(orgId)
                .orgName(org.getName())
                .totalAssets(assets.size())
                .totalOriginalValue(totalOriginal)
                .totalCurrentValue(totalCurrent)
                .subsidiaryCount(allOrgIds.size() - 1)
                .categoryDistribution(categoryStats)
                .statusDistribution(statusStats)
                .subsidiaries(subsidiaries)
                .build();
    }

    private List<Long> collectSubOrgIds(Long tenantId, Long parentId) {
        List<Organization> children = organizationMapper.selectList(
                new LambdaQueryWrapper<Organization>()
                        .eq(Organization::getTenantId, tenantId)
                        .eq(Organization::getParentId, parentId)
                        .eq(Organization::getStatus, 1)
        );

        List<Long> result = new ArrayList<>();
        for (Organization child : children) {
            result.add(child.getId());
            result.addAll(collectSubOrgIds(tenantId, child.getId()));
        }
        return result;
    }

    private List<SubsidiaryStatDTO> buildSubsidiaryStats(Long tenantId, Long parentOrgId, List<Asset> allAssets) {
        List<Organization> directChildren = organizationMapper.selectList(
                new LambdaQueryWrapper<Organization>()
                        .eq(Organization::getTenantId, tenantId)
                        .eq(Organization::getParentId, parentOrgId)
                        .eq(Organization::getStatus, 1)
        );

        Map<Long, List<Asset>> assetsByOrg = allAssets.stream()
                .collect(Collectors.groupingBy(Asset::getOrgId));

        return directChildren.stream().map(child -> {
            List<Long> childTree = collectSubOrgIds(tenantId, child.getId());
            childTree.add(child.getId());

            List<Asset> childAssets = childTree.stream()
                    .flatMap(id -> assetsByOrg.getOrDefault(id, List.of()).stream())
                    .toList();

            BigDecimal origVal = childAssets.stream()
                    .map(a -> a.getOriginalValue() != null ? a.getOriginalValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal curVal = childAssets.stream()
                    .map(a -> a.getCurrentValue() != null ? a.getCurrentValue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return SubsidiaryStatDTO.builder()
                    .orgId(child.getId())
                    .orgName(child.getName())
                    .totalAssets(childAssets.size())
                    .totalOriginalValue(origVal)
                    .totalCurrentValue(curVal)
                    .build();
        }).toList();
    }
}
