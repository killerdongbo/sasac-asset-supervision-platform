package com.sasac.platform.supervision.audit.service;

import com.sasac.platform.supervision.audit.entity.OperationLog;
import com.sasac.platform.supervision.audit.mapper.OperationLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuditServiceTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Test
    void shouldRecordAndRetrieveOperationLog() {
        OperationLog log = new OperationLog();
        log.setOperatorId(1001L);
        log.setOperatorName("张三");
        log.setAction("UPDATE");
        log.setTargetType("Asset");
        log.setTargetId(1L);
        log.setBeforeData("{\"name\":\"旧名称\"}");
        log.setAfterData("{\"name\":\"新名称\"}");
        log.setIp("192.168.1.1");
        log.setTenantId(1L);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);

        assertThat(log.getId()).isNotNull();

        List<OperationLog> logs = auditService.getLogs("Asset", 1L);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getAction()).isEqualTo("UPDATE");
        assertThat(logs.get(0).getOperatorName()).isEqualTo("张三");
    }

    @Test
    void shouldReturnEmptyLogsForNonExistentTarget() {
        List<OperationLog> logs = auditService.getLogs("Asset", 99999L);
        assertThat(logs).isEmpty();
    }

    @Test
    void shouldBuildAssetLifecycleTimeline() {
        OperationLog log1 = new OperationLog();
        log1.setOperatorId(1001L);
        log1.setOperatorName("张三");
        log1.setAction("CREATE");
        log1.setTargetType("Asset");
        log1.setTargetId(10L);
        log1.setCreatedAt(LocalDateTime.now().minusDays(10));
        operationLogMapper.insert(log1);

        OperationLog log2 = new OperationLog();
        log2.setOperatorId(1001L);
        log2.setOperatorName("张三");
        log2.setAction("UPDATE");
        log2.setTargetType("Asset");
        log2.setTargetId(10L);
        log2.setCreatedAt(LocalDateTime.now().minusDays(5));
        operationLogMapper.insert(log2);

        List<Map<String, Object>> timeline = auditService.getAssetLifecycle(10L);
        assertThat(timeline).isNotEmpty();
        assertThat(timeline).allMatch(entry -> "OPERATION".equals(entry.get("type"))
                || "FIELD_CHANGE".equals(entry.get("type")));
    }

    @Test
    void shouldReturnMultipleLogsOrderedByTime() {
        for (int i = 1; i <= 3; i++) {
            OperationLog log = new OperationLog();
            log.setOperatorId(1001L);
            log.setOperatorName("张三");
            log.setAction("UPDATE");
            log.setTargetType("Asset");
            log.setTargetId(5L);
            log.setCreatedAt(LocalDateTime.now().minusDays(3 - i));
            operationLogMapper.insert(log);
        }

        List<OperationLog> logs = auditService.getLogs("Asset", 5L);
        assertThat(logs).hasSize(3);
        assertThat(logs.get(0).getCreatedAt()).isAfterOrEqualTo(logs.get(1).getCreatedAt());
    }

    @Test
    void shouldFilterLogsByTargetType() {
        OperationLog assetLog = new OperationLog();
        assetLog.setOperatorId(1001L);
        assetLog.setOperatorName("张三");
        assetLog.setAction("UPDATE");
        assetLog.setTargetType("Asset");
        assetLog.setTargetId(1L);
        assetLog.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(assetLog);

        OperationLog taskLog = new OperationLog();
        taskLog.setOperatorId(1001L);
        taskLog.setOperatorName("张三");
        taskLog.setAction("UPDATE");
        taskLog.setTargetType("InspectionTask");
        taskLog.setTargetId(1L);
        taskLog.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(taskLog);

        List<OperationLog> assetLogs = auditService.getLogs("Asset", 1L);
        assertThat(assetLogs).hasSize(1);
        assertThat(assetLogs.get(0).getTargetType()).isEqualTo("Asset");
    }
}
