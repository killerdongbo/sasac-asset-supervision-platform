package com.sasac.platform.supervision.alert.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.supervision.alert.entity.AlertRecord;
import com.sasac.platform.supervision.alert.entity.AlertRule;
import com.sasac.platform.supervision.alert.scheduler.AlertScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AlertServiceTest {

    @Autowired
    private AlertService alertService;

    @MockBean
    private AlertScheduler alertScheduler;

    @Test
    void shouldCreateAlertRule() {
        AlertRule rule = createSampleRule();

        AlertRule created = alertService.createRule(rule);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getAlertType()).isEqualTo("INSPECTION_OVERDUE");
        assertThat(created.getRuleName()).isEqualTo("巡检逾期告警");
        assertThat(created.getEnabled()).isTrue();
    }

    @Test
    void shouldUpdateAlertRule() {
        AlertRule rule = createSampleRule();
        AlertRule created = alertService.createRule(rule);

        AlertRule update = new AlertRule();
        update.setEnabled(false);
        update.setRuleConfig("{\"daysBefore\":14}");

        AlertRule updated = alertService.updateRule(created.getId(), update);
        assertThat(updated.getEnabled()).isFalse();
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentRule() {
        AlertRule update = new AlertRule();
        update.setEnabled(false);

        assertThatThrownBy(() -> alertService.updateRule(99999L, update))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("告警规则不存在");
    }

    @Test
    void shouldListRulesByTenant() {
        AlertRule rule1 = createSampleRule();
        rule1.setTenantId(1L);
        alertService.createRule(rule1);

        AlertRule rule2 = createSampleRule();
        rule2.setRuleName("闲置资产告警");
        rule2.setAlertType("IDLE_ASSET");
        rule2.setTenantId(1L);
        alertService.createRule(rule2);

        AlertRule rule3 = createSampleRule();
        rule3.setTenantId(2L);
        alertService.createRule(rule3);

        List<AlertRule> tenant1Rules = alertService.listRules(1L, null);
        assertThat(tenant1Rules).hasSize(2);

        List<AlertRule> filteredRules = alertService.listRules(1L, "INSPECTION_OVERDUE");
        assertThat(filteredRules).hasSize(1);
    }

    @Test
    void shouldCreateAndListAlertRecords() {
        // Create parent rule first
        AlertRule rule = createSampleRule();
        AlertRule createdRule = alertService.createRule(rule);

        AlertRecord record = new AlertRecord();
        record.setRuleId(createdRule.getId());
        record.setAlertType("INSPECTION_OVERDUE");
        record.setTitle("巡检任务已逾期");
        record.setContent("巡检任务「2024年度盘点」已逾期");
        record.setAlertData("{\"taskId\":1}");
        record.setTenantId(1L);
        record.setRefId(1L);

        AlertRecord created = alertService.createRecord(record);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("ACTIVE");

        List<AlertRecord> records = alertService.listRecords(1L, null, null);
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getTitle()).isEqualTo("巡检任务已逾期");
    }

    @Test
    void shouldFilterRecordsByStatus() {
        AlertRule rule = createSampleRule();
        AlertRule createdRule = alertService.createRule(rule);

        // Create ACTIVE record
        AlertRecord active = new AlertRecord();
        active.setRuleId(createdRule.getId());
        active.setAlertType("INSPECTION_OVERDUE");
        active.setTitle("告警A");
        active.setStatus("ACTIVE");
        active.setTenantId(1L);
        alertService.createRecord(active);

        // Create RESOLVED record
        AlertRecord resolved = new AlertRecord();
        resolved.setRuleId(createdRule.getId());
        resolved.setAlertType("INSPECTION_OVERDUE");
        resolved.setTitle("告警B");
        resolved.setStatus("RESOLVED");
        resolved.setTenantId(1L);
        alertService.createRecord(resolved);

        List<AlertRecord> activeRecords = alertService.listRecords(1L, null, "ACTIVE");
        assertThat(activeRecords).hasSize(1);
        assertThat(activeRecords.get(0).getTitle()).isEqualTo("告警A");

        List<AlertRecord> allRecords = alertService.listRecords(1L, null, null);
        assertThat(allRecords).hasSize(2);
    }

    @Test
    void shouldAcknowledgeAlert() {
        AlertRule rule = createSampleRule();
        AlertRule createdRule = alertService.createRule(rule);

        AlertRecord record = new AlertRecord();
        record.setRuleId(createdRule.getId());
        record.setAlertType("INSPECTION_OVERDUE");
        record.setTitle("测试告警");
        record.setTenantId(1L);
        AlertRecord created = alertService.createRecord(record);

        alertService.acknowledge(created.getId(), 1001L);

        List<AlertRecord> records = alertService.listRecords(1L, null, "ACKNOWLEDGED");
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getHandledBy()).isEqualTo(1001L);
        assertThat(records.get(0).getHandledAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenAcknowledgingNonExistentRecord() {
        assertThatThrownBy(() -> alertService.acknowledge(99999L, 1001L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("告警记录不存在");
    }

    @Test
    void shouldResolveAlert() {
        AlertRule rule = createSampleRule();
        AlertRule createdRule = alertService.createRule(rule);

        AlertRecord record = new AlertRecord();
        record.setRuleId(createdRule.getId());
        record.setAlertType("INSPECTION_OVERDUE");
        record.setTitle("测试告警");
        record.setTenantId(1L);
        AlertRecord created = alertService.createRecord(record);

        alertService.resolve(created.getId(), 1002L);

        List<AlertRecord> records = alertService.listRecords(1L, null, "RESOLVED");
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getHandledBy()).isEqualTo(1002L);
    }

    @Test
    void shouldThrowWhenResolvingNonExistentRecord() {
        assertThatThrownBy(() -> alertService.resolve(99999L, 1002L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("告警记录不存在");
    }

    // ---- helpers ----

    private AlertRule createSampleRule() {
        AlertRule rule = new AlertRule();
        rule.setAlertType("INSPECTION_OVERDUE");
        rule.setRuleName("巡检逾期告警");
        rule.setRuleConfig("{\"daysBefore\":7}");
        rule.setEnabled(true);
        rule.setTenantId(0L);
        return rule;
    }
}
