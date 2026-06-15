package com.sasac.platform.asset.lifecycle.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LifecycleSummaryDTO {

    private long totalEvents;
    private String firstEventTime;
    private String lastEventTime;
    private long daysInService;
    private long transferCount;
    private long maintenanceCount;
    private long inspectionCount;
}
