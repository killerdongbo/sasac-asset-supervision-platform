package com.sasac.platform.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class GroupStatisticsDTO {

    private Long orgId;
    private String orgName;
    private int totalAssets;
    private BigDecimal totalOriginalValue;
    private BigDecimal totalCurrentValue;
    private int subsidiaryCount;
    private List<CategoryStat> categoryDistribution;
    private List<StatusStat> statusDistribution;
    private List<SubsidiaryStatDTO> subsidiaries;

    @Getter
    @Builder
    public static class CategoryStat {
        private String name;
        private int count;
        private BigDecimal value;
    }

    @Getter
    @Builder
    public static class StatusStat {
        private String name;
        private int count;
    }

    @Getter
    @Builder
    public static class SubsidiaryStatDTO {
        private Long orgId;
        private String orgName;
        private int totalAssets;
        private BigDecimal totalOriginalValue;
        private BigDecimal totalCurrentValue;
    }
}
