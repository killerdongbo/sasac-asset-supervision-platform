package com.sasac.platform.report.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.report.entity.Report;
import com.sasac.platform.report.mapper.ReportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ReportSubmissionService.
 * <p>
 * Tests the report submission-review-accept state machine:
 * DRAFT -> SUBMITTED -> REVIEWED -> ACCEPTED (or REJECTED back to DRAFT).
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReportSubmissionServiceTest {

    @Autowired
    private ReportSubmissionService submissionService;

    @Autowired
    private ReportMapper reportMapper;

    private Report draftReport;

    @BeforeEach
    void setUp() {
        draftReport = new Report();
        draftReport.setReportType("ASSET_SUMMARY");
        draftReport.setPeriod("2025-Q1");
        draftReport.setOrgId(1L);
        draftReport.setTenantId(0L);
        draftReport.setSubmitStatus("DRAFT");
        draftReport.setVersion(1);
        reportMapper.insert(draftReport);
    }

    @Test
    void shouldSubmitReviewAcceptHappyPath() {
        // ---- given ----
        Long reportId = draftReport.getId();
        assertThat(reportId).isNotNull();

        // ---- when: submit ----
        Report submitted = submissionService.submit(reportId, 1L);

        // ---- then: SUBMITTED ----
        assertThat(submitted.getSubmitStatus()).isEqualTo("SUBMITTED");

        // ---- when: review approved ----
        Report reviewed = submissionService.review(reportId, true, "通过审核", 2L);

        // ---- then: REVIEWED ----
        assertThat(reviewed.getSubmitStatus()).isEqualTo("REVIEWED");
        assertThat(reviewed.getReviewerId()).isEqualTo("2");
        assertThat(reviewed.getReviewRemark()).isEqualTo("通过审核");

        // ---- when: accept ----
        Report accepted = submissionService.accept(reportId);

        // ---- then: ACCEPTED ----
        assertThat(accepted.getSubmitStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    void shouldResubmitRejectedReport() {
        // ---- given ----
        Long reportId = draftReport.getId();

        // submit
        submissionService.submit(reportId, 1L);

        // reject
        submissionService.review(reportId, false, "数据需要修正", 2L);
        Report rejected = reportMapper.selectById(reportId);
        assertThat(rejected.getSubmitStatus()).isEqualTo("REJECTED");

        // ---- when: re-submit ----
        Report resubmitted = submissionService.submit(reportId, 1L);

        // ---- then ----
        assertThat(resubmitted.getSubmitStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    void shouldNotSubmitWhenNotDraftOrRejected() {
        // ---- given ----
        Long reportId = draftReport.getId();

        // submit first time
        submissionService.submit(reportId, 1L);

        // ---- when: submit again (SUBMITTED -> SUBMITTED is invalid) ----
        assertThatThrownBy(() -> submissionService.submit(reportId, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前状态不允许提交");
    }

    @Test
    void shouldNotReviewWhenNotSubmitted() {
        // ---- given ----
        Long reportId = draftReport.getId();

        // report is DRAFT, cannot review directly

        // ---- when: review without submitting first ----
        assertThatThrownBy(() -> submissionService.review(reportId, true, "审核", 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前状态不允许审核");
    }

    @Test
    void shouldNotAcceptWhenNotReviewed() {
        // ---- given ----
        Long reportId = draftReport.getId();

        // report is DRAFT, cannot accept directly

        // ---- when: accept without review ----
        assertThatThrownBy(() -> submissionService.accept(reportId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("当前状态不允许接收");
    }
}
