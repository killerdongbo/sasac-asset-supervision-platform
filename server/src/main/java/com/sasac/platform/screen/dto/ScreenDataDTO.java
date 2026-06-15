package com.sasac.platform.screen.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ScreenDataDTO {

    private OverviewData overview;
    private List<RegionAsset> regionDistribution;
    private List<StatusPie> statusDistribution;
    private List<MonthTrend> monthlyTrend;
    private List<OrgRank> topOrgs;
    private List<AlertItem> recentAlerts;

    @Data
    @Builder
    public static class OverviewData {
        private long totalAssets;
        private BigDecimal totalOriginalValue;
        private BigDecimal totalCurrentValue;
        private long monthNewAssets;
        private long totalOrgs;
        private long totalUsers;
        private int alertCount;
    }

    @Data
    @Builder
    public static class RegionAsset {
        private String name;
        private long value;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class StatusPie {
        private String name;
        private long value;
    }

    @Data
    @Builder
    public static class MonthTrend {
        private String month;
        private long count;
        private BigDecimal value;
    }

    @Data
    @Builder
    public static class OrgRank {
        private String orgName;
        private long assetCount;
        private BigDecimal totalValue;
    }

    @Data
    @Builder
    public static class AlertItem {
        private String title;
        private String level;
        private String time;
    }
}
