package com.sasac.platform.supervision.alert.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.entity.Asset;
import com.sasac.platform.asset.inspection.entity.InspectionTask;
import com.sasac.platform.asset.inspection.mapper.InspectionTaskMapper;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.mapper.AlertRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled component that runs daily scans for alert conditions.
 * <p>
 * Executes at 8:00 AM each day, checking for overdue inspection tasks
 * and idle assets that have been inactive beyond the threshold.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final AssetMapper assetMapper;
    private final AlertRecordMapper alertRecordMapper;

    /**
     * Daily scan for inspection tasks that are overdue.
     * <p>
     * Finds {@link InspectionTask} records where the end date is before
     * today and the status is not COMPLETED, then creates an
     * {@link AlertRecord} with type INSPECTION_OVERDUE for each.
     * <p>
     * Runs every day at 8:00 AM.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void scanInspectionOverdue() {
        log.info("Starting inspection overdue scan at {}", LocalDateTime.now());

        try {
            List<InspectionTask> overdueTasks = inspectionTaskMapper.selectList(
                    new LambdaQueryWrapper<InspectionTask>()
                            .lt(InspectionTask::getEndDate, LocalDate.now())
                            .ne(InspectionTask::getStatus, "COMPLETED")
            );

            int created = 0;
            for (InspectionTask task : overdueTasks) {
                // Skip if alert already exists for this task
                Long existingCount = alertRecordMapper.selectCount(
                        new LambdaQueryWrapper<AlertRecord>()
                                .eq(AlertRecord::getRefId, task.getId())
                                .eq(AlertRecord::getAlertType, "INSPECTION_OVERDUE")
                                .eq(AlertRecord::getStatus, "ACTIVE")
                );
                if (existingCount > 0) {
                    continue;
                }

                AlertRecord record = new AlertRecord();
                record.setRuleId(0L);
                record.setAlertType("INSPECTION_OVERDUE");
                record.setTitle("巡检任务已逾期");
                record.setContent("巡检任务「" + task.getTaskName() + "」截止日期为 "
                        + task.getEndDate() + "，状态为 " + task.getStatus() + "，请及时处理。");
                record.setAlertData("{\"taskId\":" + task.getId()
                        + ",\"taskName\":\"" + task.getTaskName()
                        + "\",\"endDate\":\"" + task.getEndDate() + "\"}");
                record.setStatus("ACTIVE");
                record.setTenantId(task.getTenantId());
                record.setRefId(task.getId());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                alertRecordMapper.insert(record);
                created++;
            }

            log.info("Inspection overdue scan completed. Created {} alerts.", created);
        } catch (Exception e) {
            log.error("Error during inspection overdue scan", e);
        }
    }

    /**
     * Daily scan for assets that have been idle for more than 90 days.
     * <p>
     * Finds {@link Asset} records where the usage status is IDLE and the
     * last update timestamp is older than 90 days, then creates an
     * {@link AlertRecord} with type IDLE_ASSET for each.
     * <p>
     * Runs every day at 8:00 AM.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void scanIdleAssets() {
        log.info("Starting idle asset scan at {}", LocalDateTime.now());

        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(90);

            List<Asset> idleAssets = assetMapper.selectList(
                    new LambdaQueryWrapper<Asset>()
                            .eq(Asset::getUseStatus, "IDLE")
                            .lt(Asset::getUpdatedAt, threshold)
            );

            int created = 0;
            for (Asset asset : idleAssets) {
                // Skip if alert already exists for this asset
                Long existingCount = alertRecordMapper.selectCount(
                        new LambdaQueryWrapper<AlertRecord>()
                                .eq(AlertRecord::getRefId, asset.getId())
                                .eq(AlertRecord::getAlertType, "IDLE_ASSET")
                                .eq(AlertRecord::getStatus, "ACTIVE")
                );
                if (existingCount > 0) {
                    continue;
                }

                AlertRecord record = new AlertRecord();
                record.setRuleId(0L);
                record.setAlertType("IDLE_ASSET");
                record.setTitle("闲置资产超过90天");
                record.setContent("资产「" + asset.getName() + "」（编码：" + asset.getAssetCode()
                        + "）已闲置超过90天，请核实使用状态或安排处置。");
                record.setAlertData("{\"assetId\":" + asset.getId()
                        + ",\"assetName\":\"" + asset.getName()
                        + "\",\"assetCode\":\"" + asset.getAssetCode()
                        + "\",\"updatedAt\":\"" + asset.getUpdatedAt() + "\"}");
                record.setStatus("ACTIVE");
                record.setTenantId(asset.getTenantId());
                record.setRefId(asset.getId());
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                alertRecordMapper.insert(record);
                created++;
            }

            log.info("Idle asset scan completed. Created {} alerts.", created);
        } catch (Exception e) {
            log.error("Error during idle asset scan", e);
        }
    }
}
